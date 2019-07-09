package com.example.movieknight;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movieknight.model.CastList;
import com.example.movieknight.model.Movie;
import com.example.movieknight.model.views.MovieView;
import com.example.movieknight.presenter.SingleMoviePresenter;

import java.util.ArrayList;

public class SingleMovieViewActivity extends AppCompatActivity implements MovieView {
    private static final String TAG = "MOVIE VIEW ====>";
    private static final String POSTERS_URL = "https://image.tmdb.org/t/p/w500";

    private ImageView moviePosterImageView;
    private TextView releaseDateTextView;
    private ViewPager viewPager;
    private SummaryFragment summaryFragment;
    private CastListViewFragment castListViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        Bundle bundle = getIntent().getExtras();
        Movie movieClicked = bundle.getParcelable(AllMoviesActivity.MOVIE_EXTRA);

        String titleFromMain = movieClicked.movieTitle;

        SingleMoviePresenter singleMoviePresenter = new SingleMoviePresenter(this, titleFromMain);

        moviePosterImageView = findViewById(R.id.moviePosterView);
        releaseDateTextView = findViewById(R.id.releaseTextView);
        TextView movieTitleView = findViewById(R.id.titleView);
        movieTitleView.setText(titleFromMain);


        Glide.with(getApplicationContext())
                .asBitmap()
                .load(POSTERS_URL + movieClicked.posterPath)
                .into(moviePosterImageView);

        releaseDateTextView.setText(String.format("Coming: %s",  movieClicked.releaseDate));

        //        viewPager setup
        viewPager = findViewById(R.id.viewPager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        summaryFragment = pagerAdapter.getSummaryFrag();
        castListViewFragment = pagerAdapter.getCastListViewFrag();
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        goToTab(findViewById(R.id.summaryBtn));
                        break;
                    case 1:
                        goToTab(findViewById(R.id.castBtn));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        summaryFragment.setSummaryTextViewText(movieClicked.overview);
    }

    /** MovieView Interface with the presenter:
     *   Loads image as Bitmap into the moviePosterImageView using Glide
     *
     *   sets the releaseDateTextView Text
     *
     *   and sets the summaryText on the fragment;
     *
     * @param movie the movie object back from the presenter
     */
    @Override
    public void initializeMovieView(Movie movie) {
//        Glide.with(getApplicationContext())
//                .asBitmap()
//                .load(POSTERS_URL + movie.posterPath)
//                .into(moviePosterImageView);
//
//        releaseDateTextView.setText(String.format("Coming: %s",  movie.releaseDate));
//        summaryFragment.setSummaryTextViewText(movie.overview);
    }

    /** initializes the castList fragment
     *  loops through every person in the list and gets its name
     *
     * @param castList CastList object: List of Person
     */
    @Override
    public void initializeCastListViewFragment(CastList castList) {
        ArrayList<String> cast = new ArrayList<>();
        for (int i = 0; i < castList.castList.size() && i < 10; i++) {
            cast.add(castList.castList.get(i).getName());
        }
        castListViewFragment.setCastList(cast);
//        todo: I want to implement that the list is the name and picture of the Person.
    }

//    @Override
//    public void changeViewPagerTab() {
//
//    }


    /** changes the viewPager Tab to the one Tapped by the user
     *
     * @param view Summary Button or Cast button
     */
    public void goToTab(View view) {
        Button otherBtn;
        switch (view.getId()) {
            case R.id.summaryBtn:
                viewPager.setCurrentItem(0);
                otherBtn = findViewById(R.id.castBtn);
                break;
            case R.id.castBtn:
                viewPager.setCurrentItem(1);
                otherBtn = findViewById(R.id.summaryBtn);
                break;
            default:
                otherBtn = findViewById(R.id.castBtn);
        }
        view.setBackgroundColor(getColor(R.color.colorAccent));
        otherBtn.setBackgroundColor(getColor(R.color.colorPrimaryDark));
    }
}

