package com.example.movieknight.views;

import com.example.movieknight.model.CastList;
import com.example.movieknight.model.Movie;

public interface MovieView {
    void initializeMovieView (Movie movie);
    void initializeCastListViewFragment (CastList castList);
//    void changeViewPagerTab();
}
