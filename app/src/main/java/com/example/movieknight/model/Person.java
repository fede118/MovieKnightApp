package com.example.movieknight.model;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("name")
    private final String name;
    @SerializedName("profile_path")
    private final String profilePath;

    public Person (String name, String profilePath) {
        this.name = name;
        this.profilePath = profilePath;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }
}
