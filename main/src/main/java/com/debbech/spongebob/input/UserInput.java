package com.debbech.spongebob.input;

public class UserInput {
    private String mp3Path;
    private String image;

    @Override
    public String toString() {
        return "UserInput{" +
                "mp3Path='" + mp3Path + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public UserInput(String mp3Path, String image) {
        this.mp3Path = mp3Path;
        this.image = image;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
