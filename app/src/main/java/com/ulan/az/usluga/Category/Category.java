package com.ulan.az.usluga.Category;

import java.io.Serializable;

/**
 * Created by User on 01.08.2018.
 */

public class Category implements Serializable{
    String category;
    int id;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
