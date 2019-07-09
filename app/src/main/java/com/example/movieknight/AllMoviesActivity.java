package com.example.movieknight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.movieknight.model.Movie;
import com.example.movieknight.model.MovieList;
import com.example.movieknight.views.AllMoviesView;
import com.example.movieknight.presenter.AllMoviesPresenter;


public class AllMoviesActivity extends AppCompatActivity implements AllMoviesView, RecyclerViewAdapter.OnImageClickedListener {
    private static final String TAG = "MAIN ACTIVITY=======>";
    /* Default */ static final String MOVIE_EXTRA = "movieObject";

    private ProgressBar progressBar;
    private Button retryButton;
    private AllMoviesPresenter allMoviesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movies);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView appTitleTextView = findViewById(R.id.appTitleTextView);
        appTitleTextView.setText(Html.fromHtml(title));

        allMoviesPresenter = new AllMoviesPresenter(this);
    }

    /** sets the progress bar for nowPlayingMovies Invisible and calls the Recycler Initializator
     *  function with the result as Param and the corresponding Recycler ID
     *
     * @param movieList result object back from the presenter calling the API
     */
    @Override
    public void initializeNowPlayingMoviesRecyclerView(MovieList movieList) {
        ProgressBar nowPlayingPorgressBar = findViewById(R.id.nowPlayingProgressBar);
        nowPlayingPorgressBar.setVisibility(View.INVISIBLE);

        initializeRecyclerWithIdAndMovieList(movieList, R.id.newMoviesRecycler);
    }

    /** sets the progress bar for comingSoonMovies Invisible and calls the Recycler Initializator
     *  function with the result as Param and the corresponding Recycler ID
     *
     * @param movieList result object back from the presenter calling the API
     */
//    TODO no pasar objeto MovieList, pasar la List<Movie> directamente
    @Override
    public void initializeComingSoonMoviesRecyclerView(MovieList movieList) {
        ProgressBar comingSoonProgressBar = findViewById(R.id.comingSoonProgressBar);
        comingSoonProgressBar.setVisibility(View.INVISIBLE);

        initializeRecyclerWithIdAndMovieList(movieList, R.id.comingMoviesRecycler);
    }


    /** Helper function that initializes the recyclerView given (recyclerId) with the MovieList
     *
     * @param movieList result object back from the presenter calling the API
     * @param recyclerId either nowPlayingRecycler o comingSoonRecycler
     */
    private void initializeRecyclerWithIdAndMovieList(MovieList movieList, int recyclerId) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(recyclerId);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, movieList, this::imageClickedOnRecyclerView);
        recyclerView.setAdapter(adapter);

    }

    /** RecyclerViewAdapter interface
     *
     * @param movie
     */
    @Override
    public void imageClickedOnRecyclerView(Movie movie) {
        Log.d(TAG, "movie tapped: " + movie.movieTitle);
        Intent intent = new Intent(getApplicationContext(), SingleMovieViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(MOVIE_EXTRA, movie);

        startActivity(intent);
        //todo: show progressBar when clicking a movie and waiting for activity to load
    }

    //  todo:
//    create button to retry conection with the servers

//    private void createRetryButton() {
//        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
//
//        retryButton = new Button(this);
//        retryButton.setId(View.generateViewId());
//        int buttonId = retryButton.getId();
//        retryButton.setText(R.string.retry_button);
//        retryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//
//                try {
//                    networkCall(NEW_MOVIES_URL, newMovies, R.id.newMoviesRecycler);
//                    networkCall(COMING_SOON_MOVIES_URL, comingSoonMovies, R.id.comingMoviesRecycler);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                retryButton.setVisibility(View.INVISIBLE);
//                ((ViewManager) retryButton.getParent()).removeView(retryButton);
//            }
//        });
//        layout.addView(retryButton,0);
//
//        ConstraintSet set = new ConstraintSet();
//        set.clone(layout);
//
//        set.connect(buttonId, ConstraintSet.TOP, R.id.appTitleTextView,ConstraintSet.BOTTOM, 32);
//        set.connect(buttonId, ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
//        set.connect(buttonId, ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
//        set.applyTo(layout);
//    }
}
