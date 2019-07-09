package com.example.movieknight.presenter;

import com.example.movieknight.model.Movie;
import com.example.movieknight.model.MovieList;
import com.example.movieknight.model.views.AllMoviesView;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class AllMoviesPresenter {
    private static final String TAG = "MOVIE PRESENTER";


    private static final String API_URL = "https://api.themoviedb.org";
    private static final String API_KEY = "15d2ea6d0dc1d476efbca3eba2b9bbfb";
    //    String creditsUrl = "https://api.themoviedb.org/3/movie/" + result.getString("id") + "/credits?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&language=en-US";

    private final AllMoviesView allMoviesView;

    private Retrofit retrofit;

//    TODO: crear interfaz MOVIEAPI (en paquete api) y estos dos interface ahi y despues crear
//    clase interactor con metodo execute para uno de cada de los metodos
    public interface TheMovieDBGetNowPlayingMovies {
        @GET("/3/movie/now_playing")
        Observable<MovieList> MovieList(
                @Query("api_key") String apiKey);
    }

    public interface TheMovieDBGetComingSoonMovies {
        @GET("/3/movie/upcoming")
        Observable<MovieList> MovieList(
                @Query("api_key") String apiKey);
    }


//    TODO: el conantuctor tiene que tenr la logica afuera y crear metodos sincronizados con
//    los lifecycles del vieew
    public AllMoviesPresenter(final AllMoviesView allMoviesView){
        this.allMoviesView = allMoviesView;

        OkHttpClient httpClient = new OkHttpClient();

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            TheMovieDBGetNowPlayingMovies theMovieDBGetNowPlayingMovies = retrofit.create(TheMovieDBGetNowPlayingMovies.class);

            Observable<MovieList> nowPlayingMoviesobservable = theMovieDBGetNowPlayingMovies.MovieList(API_KEY);

            nowPlayingMoviesobservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onGetNowPlayingMoviesResponseReceived);

            TheMovieDBGetComingSoonMovies theMovieDBGetComingSoonMovies = retrofit.create(TheMovieDBGetComingSoonMovies.class);

            Observable<MovieList> comingSoonMoviesObservable = theMovieDBGetComingSoonMovies.MovieList(API_KEY);

            comingSoonMoviesObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onGetComingSoonMoviesResponseReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void onGetNowPlayingMoviesResponseReceived(MovieList movieList) {
        allMoviesView.initializeNowPlayingMoviesRecyclerView(movieList);
    }

    private void onGetComingSoonMoviesResponseReceived(MovieList movieList) {
        allMoviesView.initializeComingSoonMoviesRecyclerView(movieList);
    }
}
