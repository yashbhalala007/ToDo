package com.example.todo;

import android.net.Uri;

public class User {
    public String name, email;
    public Uri profileImage;


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public User(String name, String email, Uri profileImage) {
        this.name = name;
        this.email = email;
        this.profileImage=profileImage;
    }
}
