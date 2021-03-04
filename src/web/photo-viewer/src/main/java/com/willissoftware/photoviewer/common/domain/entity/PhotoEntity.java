package com.willissoftware.photoviewer.common.domain.entity;

import javax.persistence.*;

@Entity(name = "PhotoEntity")
@Table(name="photo")
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long photoId;
    private String filename;
    private int likes;       // >1 for like, <1 for not liked 0 for nothing (shouldn't really be stored

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

}
