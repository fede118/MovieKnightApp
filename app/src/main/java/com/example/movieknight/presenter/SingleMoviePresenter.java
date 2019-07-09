package com.example.movieknight.presenter;

import com.example.movieknight.model.CastList;
import com.example.movieknight.model.Movie;
import com.example.movieknight.model.MovieList;
import com.example.movieknight.views.MovieView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SingleMoviePresenter {
    private static final String TAG = "SINGLE MOVIE PRESENTER";

    private static final String API_URL = "https://api.themoviedb.org";
    private static final String API_KEY = "15d2ea6d0dc1d476efbca3eba2b9bbfb";

    private final MovieView movieView;
    private final String movieTitle;

    private Retrofit retrofit;
    public static OkHttpClient httpClient;

    public interface TheMovieDBGetMovie {
        @GET("/3/search/movie")
        Observable<MovieList> MovieList(
                @Query("api_key") String apiKey,
                @Query("query") String movieTitle);
    }

    public interface TheMovieDBGetCast {
        @GET("/3/movie/{id}/credits?")
        Observable<CastList> CastList(
                @Path("id") String movieId,
                @Query("api_key") String apiKey);

    }

    public SingleMoviePresenter(final MovieView movieView, String movieTitle) {
        this.movieView = movieView;
        this.movieTitle = movieTitle;

        httpClient = new OkHttpClient();

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            TheMovieDBGetMovie theMovieDB = retrofit.create(TheMovieDBGetMovie.class);

            Observable<MovieList> observable = theMovieDB.MovieList(API_KEY, movieTitle);

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onGetMovieResponseReceived);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** gets the first element of the list of movies (supposedly the correct one)
     * it formats the Movie.releaseDate with formatReleaseDate function
     * it calls the interface initializeMovieVIew() with the Movie object as param
     * and starts the function that get the castList (getCastListWithMovieId())
     *
     * @param response MovieList object containing a List of Movie objects

     *
     */
    private void onGetMovieResponseReceived(MovieList response) {
        Movie movie = response.result.get(0);
        movie.setReleaseDate(formatReleaseDate(movie.releaseDate));
        movieView.initializeMovieView(movie);
        getCastListWithMovieId(retrofit, movie.movieId);
    }


    /** creates an implementation of the retrofit instance with the params of the
     *  service interface TheMovieDBGetCast and subscribes the observer to get the CastList
     *  from TheMovieDB API. On response it executes onGetCastResponseReceived
     *
     * @param retrofit Retrofit instance
     * @param movieId String with the movieId assigned by TheMovieDb
     *
     */
    private void getCastListWithMovieId(Retrofit retrofit, String movieId) {
        TheMovieDBGetCast theMovieDBGetCast = retrofit.create(TheMovieDBGetCast.class);

        Observable<CastList> castListObservable = theMovieDBGetCast.CastList(movieId, API_KEY);

        castListObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetCastResponseReceived);
    }


    /**it calls the interface initializeCastListViewFragment withe the castList as Param.
     *
     * @param castList CastList object containing a List of Person Objects
     */
    private void onGetCastResponseReceived(CastList castList) {
//        deberia hacer el for loop para armar el array aca o en SingleMovieViewActivity?
        movieView.initializeCastListViewFragment(castList);
    }

    /** turns a string representing a date in format "yyyy-MM-dd" to "d MMM"
     *  Ex.: 1991-03-26 ==> 26 MAR
     *
     * @param inputDate String representing a date ("yyyy-MM-dd")
     * @return String representing a date in "d MMM" format
     */
    private String formatReleaseDate(String inputDate) {
        String formatingDate = inputDate.replaceAll("\"","");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(formatingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
    }
}

