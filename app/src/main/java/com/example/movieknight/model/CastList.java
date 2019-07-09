package com.example.movieknight.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastList {
    @SerializedName("cast")
    public List<Person> castList;
}
