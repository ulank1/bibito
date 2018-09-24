package com.ulan.az.usluga.order;

import com.ulan.az.usluga.User;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

/**
 * Created by User on 04.08.2018.
 */

public class Order implements Serializable {

    String image,address,category;
    GeoPoint geoPoint;
    User user;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
