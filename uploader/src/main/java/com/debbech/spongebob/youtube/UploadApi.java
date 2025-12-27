package com.debbech.spongebob.youtube;


import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;

public class UploadApi {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private YoutubeVideo video;
    private String user_access_token;
    private  String locationUpload = null;

    public void doUpload(YoutubeVideo yv, String accessToken) throws Exception{
        log.info("starting effective upload at "+  LocalDateTime.now());
        this.video = yv;
        this.user_access_token = accessToken;
        try {
            this.locationUpload = startResumableSession();
            long lastByte = 0;
            uploadVideo(lastByte);
            /*while(true) {
                if(uploadVideo(lastByte) == false){
                    lastByte = checkStatus();
                    lastByte = lastByte+1;
                }else{
                    break;
                }
            }*/
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
                //.url("https://www.googleapis.com/upload/youtube/v3/videos?uploadType=resumable&part=snippet,status,contentDetails")
                .url("http://localhost:4000/videos?uploadType=resumable&part=snippet,status,contentDetails")
                .post(body)
                .header("Authorization", "Bearer " + this.user_access_token)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Content-Length", String.valueOf(bodyj.length()))
                .header("X-Upload-Content-Length", String.valueOf(this.video.getSizeInBytes()))
                .header("X-Upload-Content-Type", "video/*")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(response.code() < 299) {
                log.info(response.code() + " OK from starting resumable upload session");
                log.error(response.body().string());
                String location = response.header("Location");
                if (location == null) {
                    log.error("Location URL that will be used for next api calls is not found");
                    throw new Exception("Location URL that will be used for next api calls is not found");
                }
                log.info("Location: " + location);
                return location;
            }else{
                log.info("received "  + response.code() + " from google when starting upload session");
                log.error(response.body().string());
                throw new Exception("could not start resumable session to upload the video because received " + response.code()+ " " + response.body().string());
            }
        } catch (IOException e) {
            log.error("could not start resumable session to upload the video");
            throw new Exception("could not start resumable session to upload the video: " + e.getMessage());
        }
    }

    private boolean uploadVideo(long start_byte) throws Exception{

        File requestBodyFile;
        if(start_byte == 0){
            requestBodyFile = new File(this.video.getPathOnDisk());
        }else{
            RandomAccessFile sourceFile =new RandomAccessFile(this.video.getPathOnDisk(), "r");
            sourceFile.seek(start_byte + 1);   // jump to this byte

            byte[] buffer = new byte[4096];
            RandomAccessFile raf = new RandomAccessFile("filefrom"+start_byte, "rw");
            while ((sourceFile.read(buffer)) != -1) {
                raf.write(buffer);
            }
            raf.close();
            sourceFile.close();
            requestBodyFile = new File("filefrom"+start_byte);
            System.err.println(requestBodyFile.length());
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("video/mp4"),
                requestBodyFile
        );

        Request request;
        if(start_byte != 0){
            System.out.println("Resuming upload after byte " + (start_byte -1));
            request = new Request.Builder()
                    .url(this.locationUpload)
                    .put(requestBody)
                    .header("Authorization", "Bearer " + this.user_access_token)
                    .header("Content-Type", "video/*")
                    .header("Content-Length", String.valueOf(this.video.getSizeInBytes()-(start_byte+1)))
                    .header("Content-Range", "bytes "+ start_byte+"-"+(this.video.getSizeInBytes()-1)+"/"+(this.video.getSizeInBytes()))
                    .build();
        }else{
            request = new Request.Builder()
                    .url(this.locationUpload)
                    .put(requestBody)
                    .header("Authorization", "Bearer " + this.user_access_token)
                    .header("Content-Type", "video/*")
                    .header("Content-Length", String.valueOf(this.video.getSizeInBytes()))
                    .build();
        }

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if(response.code() < 299) {
                System.out.println("Upload finished with " + response.code() + " code!");
                return true;
            }else{
                if((response.code() >= 500) && (response.code() <= 504)){
                    System.out.println("The upload has been interrupted and stoped.. retrying in few moments.");
                }else {
                    System.err.println("Upload failed permanently with " + response.code() + " code!");
                    System.err.println(response.body().string());
                }
            }
            return false;
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

