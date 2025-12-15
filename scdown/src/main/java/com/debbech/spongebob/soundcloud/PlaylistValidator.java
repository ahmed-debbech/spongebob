package com.debbech.spongebob.soundcloud;

import com.debbech.spongebob.SpongeBob;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaylistValidator {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public List<Data.Track> validate(String playlistUrl) throws Exception {

        List<Data.Track> tracklist = new ArrayList<>();

        if(playlistUrl == null) throw new Exception("playlist url is null");
        if(playlistUrl.isEmpty()) throw new Exception("playlist url is empty");
        if(!playlistUrl.startsWith("https://soundcloud.com")) throw new Exception("playlist link is not soundcloud");

        String html = getHtml(playlistUrl);

        String tag = getHydrationTag(html);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Hydration>>() {}.getType();
        List<Hydration> playlist = gson.fromJson(tag, listType);
        for(Hydration hy : playlist){
            if(hy.data instanceof Map){
                Data obj = gson.fromJson(gson.toJson(hy.data), Data.class);
                if(obj.tracks != null) {
                    tracklist = obj.tracks;
                    tracklist = getRemainingTracksData(tracklist, html);
                    CurrentPlaylist.getInstance().setPlaylistUrl(playlistUrl);
                    CurrentPlaylist.getInstance().setTrackList(tracklist);
                    return tracklist;
                }
            }
        }

        throw new Exception("found no tracklist in playlist");
    }

    private String getClientId(String html) throws Exception{

        Exception e = new Exception("SERVER ERROR: could not determine client_id while getting remaining tracks data");
        Document doc = Jsoup.parse(html);
        Elements scripts = doc.select("script[src]");
        Element lastScript = scripts.last();
        if(lastScript == null) throw e;
        String url = lastScript.attr("src");
        if(url == null || url.isEmpty()) throw e;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw e;

            String text = response.body().string();
            Pattern p = Pattern.compile(",client_id:\"([^\"]*?.[^\"]*?)\"");
            Matcher m = p.matcher(text);
            if(!m.find()) throw e;
            String match  = text.substring(m.start(), m.end()-1).split("\"")[1];
            return match;
        }
    }
    private List<Data.Track> getRemainingTracksData(List<Data.Track> trackList, String html) throws Exception{
        String client_id = getClientId(html);

        for(int i = 0; i <= trackList.size()-1; i++) {

            if(trackList.get(i).permalink_url!=null) continue;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api-v2.soundcloud.com/tracks/" + trackList.get(i).id + "?client_id=" + client_id)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new Exception("soundcloud returned not 200 when retrieving a track data "+ response.body().string() );

                Gson g = new Gson();
                Data.Track tr = g.fromJson(response.body().string(), Data.Track.class);
                trackList.set(i, tr);
            }
        }
        return trackList;
    }

    private String getHydrationTag(String html) throws  Exception{
        Document doc = Jsoup.parse(html);
        Elements scriptTags = doc.getElementsByTag("script");
        for(Element el : scriptTags){
            if(el.html().contains("__sc_hydration")) {
                String finHtml = el.html().substring(24, el.html().length()-1);
                return finHtml;
            }
        }
        throw new Exception("could not find __sc_hydration object from soundcloud");
    }

    private String getHtml(String link) throws Exception {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("soundcloud returned error when requesting playlist html");

            return response.body().string();
        }catch (Exception e){
            throw e;
        }
    }
}
