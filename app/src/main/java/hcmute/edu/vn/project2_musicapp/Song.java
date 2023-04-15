package hcmute.edu.vn.foregroundservice;

import java.io.Serializable;

public class Song implements Serializable {

    private String title;
    private String single;
    private int image;
    private int resouce;

    public Song(String title, String single, int image, int resouce) {
        this.title = title;
        this.single = single;
        this.image = image;
        this.resouce = resouce;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResouce() {
        return resouce;
    }

    public void setResouce(int resouce) {
        this.resouce = resouce;
    }
}