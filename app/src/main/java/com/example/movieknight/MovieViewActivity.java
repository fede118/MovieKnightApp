package com.example.movieknight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG = "MOVIE VIEW ====>";

    public ViewPager pager;
    public PagerAdapter pagerAdapter;
    public static String summaryString;
    public JSONObject movieJson;
    ImageView moviePoster;
    TextView releaseDate;
    Bitmap bitmapPoster;

//  todo:
    public JSONObject movieCreditsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        Intent intent = getIntent();
        String titleFromMain = intent.getStringExtra(RecyclerViewAdapter.TITLE);

        moviePoster = findViewById(R.id.moviePosterView);
        releaseDate = findViewById(R.id.releaseTextView);

        byte[] byteArray = intent.getByteArrayExtra(RecyclerViewAdapter.BITMAP_POSTER);
        bitmapPoster = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        moviePoster.setImageBitmap(bitmapPoster);

        //        viewPager setup
        pager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

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
            public void onPageScrollStateChanged(int i) {

            }
        });



        TextView movieTitleView = findViewById(R.id.titleView);
        movieTitleView.setText(titleFromMain);

        String url = "https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + titleFromMain + "&callback=?";

        try {
            movieJson = new getJson("results").execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (movieJson != null) {
            try {
//            Setting release Date
                String stringDate = movieJson.getString("release_date").replaceAll("\"","");
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(stringDate);
                String dateTransf = new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
                releaseDate.setText(String.format("Coming: %s",  dateTransf));

//              set summary
                summaryString = movieJson.getString("overview");

//               todo: setting cast
                Log.d(TAG, "movie id: " +  movieJson.getString("id"));
                String creditsUrl = "https://api.themoviedb.org/3/movie/" + movieJson.getString("id") + "/credits?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&language=en-US";
                movieCreditsJson = new getJson("cast").execute(creditsUrl).get();

//                Todo: por como hice el getJSON solo saca el primer elemento del array (porque en las peliculas siempre queremos el primer resultado de la busqueda, en este caso no

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(movieCreditsJson != null) {
            Log.d(TAG, movieCreditsJson.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //    async task que accede al API the themoviedb.org para obtener informacion acerca de la pelicula seleccionada
    public class getJson extends AsyncTask<String, Void, JSONObject> {
//    jsonPrimaryKey seria la Key que contiene la informacion que queremos del json (por como viene formateada de la API
    private String jsonPrimaryKey;

    private getJson(String jsonKey) {
        super();

        jsonPrimaryKey = jsonKey;
    }

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

                JSONObject json;
//                algunas veces el desde la Api el json empieza con "?(" y termina ")"
                if (s.substring(0, 2).equals("?(")) {
                    json = new JSONObject(s.substring(2, s.lastIndexOf(')')));
                } else {
                    json = new JSONObject(s);
                }

                String results = json.getString(jsonPrimaryKey);

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



//    goes to selected tab
    public void goToTab(View view) {
        Button otherBtn;
        switch (view.getId()) {
            case R.id.summaryBtn:
                pager.setCurrentItem(0);
                otherBtn = findViewById(R.id.castBtn);
                break;
            case R.id.castBtn:
                pager.setCurrentItem(1);
                otherBtn = findViewById(R.id.summaryBtn);
                break;
            default:
                otherBtn = findViewById(R.id.castBtn);
        }
        view.setBackgroundColor(getColor(R.color.colorAccent));
        otherBtn.setBackgroundColor(getColor(R.color.colorPrimaryDark));
    }
}

