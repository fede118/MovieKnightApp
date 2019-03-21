package com.example.movieknight;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MovieViewActivity extends AppCompatActivity {
    String posterPath;
    ImageView moviePoster;
    TextView releaseDate;
    TextView summaryText;

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
//            new getJson().execute(url).get();
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
    }

//    public class getJson extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... urls) {
//            return MainActivity.getJson(urls);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            try {
//                JSONObject json = new JSONObject(s.substring(2, s.lastIndexOf(')')));
//
//                String results = json.getString("results");
//
//                JSONArray jsonArray = new JSONArray(results);
//
//                if (jsonArray.length() > 0) {
//                    JSONObject firstResult = jsonArray.getJSONObject(0);
//
////                    setting poster
//                    posterPath = firstResult.getString("poster_path");
//                    downloadImage(posterPath);
//
////                    Setting release Date
//                    String stringDate = firstResult.getString("release_date").replaceAll("\\\"","");
//                    Date date = new SimpleDateFormat("YYYY-MM-DD", Locale.ROOT).parse(stringDate);
//                    String dateTransf = new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
//
//                    releaseDate.setText("Coming: " + dateTransf);
//
////                    set summary
//                    summaryText.setText(firstResult.getString("overview"));
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
}
