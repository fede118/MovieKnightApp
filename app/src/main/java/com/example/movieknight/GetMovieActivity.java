package com.example.movieknight;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.movieknight.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class GetMovieActivity extends AppCompatActivity {
    public static final String API_URL = "https://api.themoviedb.org";

    public static OkHttpClient httpClient;

//    public static class Movie {
//        @SerializedName("title")
//        public final String movieTitle;
//        @SerializedName("poster_path")
//        public final String posterPath;
//
//
//        public Movie(String movieTitle, String posterPath){
//            this.movieTitle = movieTitle;
//            this.posterPath = posterPath;
//        }
//    }

    public static class MovieList {
        @SerializedName("results")
        List<Movie> movieList;
    }

    public interface TheMovieDB {
        @GET("/3/search/movie")
        Observable<MovieList> MovieList(
                @Query("api_key") String apiKey,
                @Query("query") String movieTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_movie_activity);

        httpClient = new OkHttpClient();

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            TheMovieDB theMovieDB = retrofit.create(TheMovieDB.class);

            Observable<MovieList> observable = theMovieDB.MovieList("15d2ea6d0dc1d476efbca3eba2b9bbfb", "gladiator");

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MovieList>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(MovieList response) {
                            for (int i = 0; i < response.movieList.size(); i++) {
                                Log.d(response.movieList.get(i).movieTitle + " posterPath ==>",
                                        "" + response.movieList.get(i).posterPath);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("FAILED =======>", e.toString());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("onComplete ======>", "complete?");
                            //                            List<Movie> movies = response.body().movieList;
//                            Log.d("RESPONSE ===>", movies.toString());
//                            for (int i = 0; i < response.body().movieList.size(); i++) {
//                                Log.d(movies.get(i).movieTitle + " posterPath ==>" , "" + movies.get(i).posterPath);
//                            }
                        }
                    });

//            call.enqueue(new Callback<MovieList>() {
//                @Override
//                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
//                    Log.d("CALL ===>", call.toString());
//                    List<Movie> movies = response.body().movieList;
//                    Log.d("RESPONSE ===>", movies.toString());
//                    for (int i = 0; i < response.body().movieList.size(); i++) {
//                        Log.d(movies.get(i).movieTitle + " posterPath ==>" , "" + movies.get(i).posterPath);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MovieList> call, Throwable t) {
//
//                    Log.d("FAILED", t.toString());
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
