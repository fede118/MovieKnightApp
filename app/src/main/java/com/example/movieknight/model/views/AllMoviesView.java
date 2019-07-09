package com.example.movieknight.model.views;

import com.example.movieknight.model.Movie;
import com.example.movieknight.model.MovieList;

public interface AllMoviesView {
    void initializeNowPlayingMoviesRecyclerView(MovieList movieList);
    void initializeComingSoonMoviesRecyclerView(MovieList movieList);

    void imageClickedOnRecyclerView(Movie movie);
}
