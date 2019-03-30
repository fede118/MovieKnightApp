package com.example.movieknight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MovieViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    ImageView moviePoster;
    TextView releaseDate;
    TextView summaryText;

    JSONObject movieJson;

    int imgIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        Intent intent = getIntent();
        String titleFromMain = intent.getStringExtra("title");

        moviePoster = findViewById(R.id.moviePosterView);
        releaseDate = findViewById(R.id.releaseTextView);
        summaryText = findViewById(R.id.summaryView);
        summaryText.setMovementMethod(new ScrollingMovementMethod());

        TextView movieTitleView = findViewById(R.id.titleView);
        movieTitleView.setText(titleFromMain);

        String url = "https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + titleFromMain + "&callback=?";
        try {
            movieJson = new getJson().execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (MainActivity.newMovies.contains(titleFromMain)) {
            imgIndex = MainActivity.newMovies.indexOf(titleFromMain);
            downloadImage(MainActivity.imageUrls.get(imgIndex));
        } else if (MainActivity.comingSoonMovies.contains(titleFromMain)) {
            imgIndex = MainActivity.comingSoonMovies.indexOf(titleFromMain);
            downloadImage(MainActivity.comingImageUrls.get(imgIndex));
        }

        if (movieJson != null) {
            System.out.println(movieJson.toString());
//        Setting release Date
            try {
                String stringDate = movieJson.getString("release_date").replaceAll("\"","");
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(stringDate);
                String dateTransf = new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
                releaseDate.setText("Coming: " + dateTransf);
                //                    set summary
                summaryText.setText(movieJson.getString("overview"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        TOOLBAR and TABS Setup
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        viewPager = findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
//
//        tabLayout = findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
    }

    public class getJson extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            StringBuilder res = new StringBuilder();
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();


                while (data != -1) {
                    char current = (char) data;
                    res.append(current);
                    data = reader.read();
                }

                String s = res.toString();

                JSONObject json = new JSONObject(s.substring(2, s.lastIndexOf(')')));

                String results = json.getString("results");

                JSONArray jsonArray = new JSONArray(results);

                if (jsonArray.length() > 0) {
                    return jsonArray.getJSONObject(0);
                } else {
                    return new JSONObject();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return new JSONObject();
        }
    }

    public void downloadImage (String posterPath) {
        ImageDownloader task = new ImageDownloader();

        Bitmap myImage;
        try {
            myImage = task.execute("https://image.tmdb.org/t/p/w500" + posterPath).get();

            moviePoster.setImageBitmap(myImage);

        } catch (ExecutionException e) {
            Log.i("Exception", "EXECUTION");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.i("Exception", "INTERRUPTION");
            e.printStackTrace();
        } catch (Exception e ) {
            Log.i("Exception", "GENERAL");
            e.printStackTrace();
        }
    }

    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in = connection.getInputStream();

                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

//    toolbar and tabs helpers
//    private void setupViewPager (ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
////        esos eran dummy fragments
//        adapter.addFragment(new Fragment(), "ONE");
//        adapter.addFragment(new Fragment(), "TWO");
//        adapter.addFragment(new Fragment(), "THREE");
//    }
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter (FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//        public void addFragment (Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
}

