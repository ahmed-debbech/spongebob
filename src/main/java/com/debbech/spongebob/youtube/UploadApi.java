package com.debbech.spongebob.youtube;


import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;

public class UploadApi {

    private YoutubeVideo video;
    private String user_access_token;
    private  String locationUpload = null;

    public void doUpload(YoutubeVideo yv, String accessToken) throws Exception{
        System.out.println("starting effective upload at "+  LocalDateTime.now());
        try {
            this.locationUpload = startResumableSession();
            uploadVideo();
        }catch (Exception e){
            throw e;
        }
    }

    private String startResumableSession() throws Exception {

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String bodyj = """
                {
                  "snippet": {
                    "title": "My video title",
                    "description": "This is a description of my video",
                    "tags": [],
                    "categoryId": 24
                  },
                  "status": {
                    "privacyStatus": "unlisted",
                    "embeddable": true,
                    "license": "youtube"
                  }
                }
                """;
        RequestBody body = RequestBody.create(JSON, bodyj);

        Request request = new Request.Builder()
                .url("https://www.googleapis.com/upload/youtube/v3/videos?uploadType=resumable&part=snippet,status,contentDetails")
                .post(body)
                .header("Authorization", "Bearer " + this.user_access_token)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Content-Length", String.valueOf(bodyj.length()))
                .header("X-Upload-Content-Length", String.valueOf(this.video.getSizeInBytes()))
                .header("X-Upload-Content-Type", "video/*")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("200 OK from starting resumable upload session");
            String location = response.header("Location");
            if(location == null){
                System.err.println("Location URL that will be used for next api calls is not found");
                throw new Exception("Location URL that will be used for next api calls is not found");
            }
            System.out.println("Location: " + location);
            return location;
        } catch (IOException e) {
            System.err.println("could not start resumable session to upload the video");
            throw new Exception("could not start resumable session to upload the video: " + e.getMessage());
        }
    }

    private void uploadVideo(){

    }
    private void checkStatus(){

    }

}

