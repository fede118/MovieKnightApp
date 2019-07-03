package com.example.movieknight.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

public class Movie {
    @SerializedName("title")
    public final String movieTitle;
    @SerializedName("poster_path")
    public final String posterPath;
    @SerializedName("release_date")
    public final String releaseDate;
    @SerializedName("overview")
    public final String overview;
    @SerializedName("cast")
    public JSONArray cast;


    public Movie(String movieTitle, String posterPath, String releaseDate, String overview){
        this.movieTitle = movieTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    public void setCast(JSONArray cast) {
        this.cast = cast;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }
}
