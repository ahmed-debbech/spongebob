package com.debbech.spongebob.youtube;


import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class UploadApi {

    private YoutubeVideo video;
    private String user_access_token;
    private  String locationUpload = null;

    public void doUpload(YoutubeVideo yv, String accessToken) throws Exception{
        System.out.println("starting effective upload at "+  LocalDateTime.now());
        this.video = yv;
        this.user_access_token = accessToken;
        try {
            this.locationUpload = startResumableSession();
            //for testing only the new thread
            new Thread(()-> {
                while(true) {
                    try {
                        checkStatus();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
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
            if(response.code() < 299) {
                System.out.println(response.code() + " OK from starting resumable upload session");
                System.err.println(response.body().string());
                String location = response.header("Location");
                if (location == null) {
                    System.err.println("Location URL that will be used for next api calls is not found");
                    throw new Exception("Location URL that will be used for next api calls is not found");
                }
                System.out.println("Location: " + location);
                return location;
            }else{
                System.out.println("received "  + response.code() + " from google when starting upload session");
                System.err.println(response.body().string());
                throw new Exception("could not start resumable session to upload the video because received " + response.code()+ " " + response.body().string());
            }
        } catch (IOException e) {
            System.err.println("could not start resumable session to upload the video");
            throw new Exception("could not start resumable session to upload the video: " + e.getMessage());
        }
    }

    private void uploadVideo() throws Exception{
        File sourceFile =new File(this.video.getPathOnDisk());

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("video/mp4"),
                sourceFile
        );

        Request request = new Request.Builder()
                .url(this.locationUpload)
                .put(requestBody)
                .header("Authorization", "Bearer " + this.user_access_token)
                .header("Content-Type", "video/*")
                .header("Content-Length", String.valueOf(this.video.getSizeInBytes()))
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if(response.code() < 299) {
                System.out.println("Upload finished with " + response.code() + " code!");
            }else{
                if((response.code() <= 500) && (response.code() <= 504)){
                    System.out.println("The upload has been interrupted and stoped.. retrying in few moments.");
                    long lastByte = checkStatus();
                    //todo we repeat that
                }else {
                    System.err.println("Upload failed permanently with " + response.code() + " code!");
                    System.err.println(response.body().string());
                }
            }
        } catch (Exception e) {
            System.err.println("could not upload the video to youtube, connection interrupted");
            throw new Exception("could not upload the video to youtube, connection interrupted : "+ e.getMessage());
        }
    }
    private long checkStatus() throws Exception{

        RequestBody emptyBody = RequestBody.create(null, new byte[0]);

        Request request = new Request.Builder()
                .url(this.locationUpload)
                .put(emptyBody)
                .header("Authorization", "Bearer " + this.user_access_token)
                .header("Content-Range", "bytes */" + String.valueOf(this.video.getSizeInBytes()))
                .header("Content-Length", "0")
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if(response.code() == 308) {
                if(response.header("Range") != null) {
                    long firstbyte = Long.parseLong(response.header("Range").split("=")[1].split("-")[0]);
                    long secondbyte = Long.parseLong(response.header("Range").split("=")[1].split("-")[1]);
                    System.out.println("video session retreived and it is partially uploaded " + firstbyte + "-" + secondbyte);
                    return secondbyte;
                }else{
                    System.out.println("no bytes where upload yet to this session");
                    return 0;
                }
            }
            throw new Exception("not 308 code, so could not check if upload has finished");
        } catch (Exception e) {
            System.err.println("could not upload the video to youtube, connection interrupted");
            throw new Exception("could not upload the video to youtube, connection interrupted : "+ e.getMessage());
        }
    }

    private void retryUpload(long remaining){

    }
}

