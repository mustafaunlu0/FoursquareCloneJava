package com.mustafaunlu.foursquareclone.model;

import android.graphics.Bitmap;

public class Place {
    private static Place instance;

    private Bitmap image;
    private String name;
    private String type;
    private String atmosphere;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public static Place getInstance(){
        if(instance == null){
            instance=new Place();
        }
        return instance;
    }
}
