package com.ulan.az.usluga;

import java.io.Serializable;

/**
 * Created by User on 01.08.2018.
 */

public class User implements Serializable  {

    String name,age,phone,image, deviceId;
    int id;
    int id_confirm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getId_confirm() {
        return id_confirm;
    }

    public void setId_confirm(int id_confirm) {
        this.id_confirm = id_confirm;
    }
}
