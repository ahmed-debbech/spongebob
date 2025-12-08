package com.debbech.spongebob.youtube;

public class YoutubeVideo {
    private String pathOnDisk;
    private long sizeInBytes;

    public YoutubeVideo(String pathOnDisk, long sizeInBytes) {
        this.pathOnDisk = pathOnDisk;
        this.sizeInBytes = sizeInBytes;
    }

    public String getPathOnDisk() {
        return pathOnDisk;
    }

    public void setPathOnDisk(String pathOnDisk) {
        this.pathOnDisk = pathOnDisk;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
}
