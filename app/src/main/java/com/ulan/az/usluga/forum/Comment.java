package com.ulan.az.usluga.forum;

import com.ulan.az.usluga.User;

/**
 * Created by User on 20.08.2018.
 */

public class Comment {

    String comment, date;
    User user;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
