package com.example.movieknight.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

public class Movie implements Parcelable {
    @SerializedName("title")
    public String movieTitle;
    @SerializedName("id")
    public String movieId;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("overview")
    public String overview;
    @SerializedName("cast")
    public JSONArray cast;


    public Movie (String movieTitle, String movieId, String posterPath, String releaseDate, String overview)  {
        this.movieTitle = movieTitle;
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    public Movie(Parcel in){
        readFromParcel(in);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //    hace falta cast? no esta siendo usado
    public void setCast(JSONArray cast) {
        this.cast = cast;
    }

    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieId() { return movieId;}

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(movieId);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(overview);
    }

    private void readFromParcel(Parcel in) {
        this.movieTitle = in.readString();
        this.movieId = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.overview = in.readString();
    }
}
