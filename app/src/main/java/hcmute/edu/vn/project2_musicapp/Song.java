package hcmute.edu.vn.project2_musicapp;

import java.io.Serializable;

public class Song implements Serializable {

    private int id;
    private String nameMusic;
    private String nameSinger;
    private String image;
    private String resouce;

    public Song() {
    }

    public Song(int id, String nameMusic, String nameSinger, String image, String resouce) {
        this.id = id;
        this.nameMusic = nameMusic;
        this.nameSinger = nameSinger;
        this.image = image;
        this.resouce = resouce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameMusic() {
        return nameMusic;
    }

    public void setNameMusic(String nameMusic) {
        this.nameMusic = nameMusic;
    }

    public String getNameSinger() {
        return nameSinger;
    }

    public void setNameSinger(String nameSinger) {
        this.nameSinger = nameSinger;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getResouce() {
        return resouce;
    }
    public void setResouce(String resouce) {
        this.resouce = resouce;
    }
}