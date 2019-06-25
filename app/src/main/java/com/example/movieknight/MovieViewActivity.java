package com.example.movieknight;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG = "MOVIE VIEW ====>";

    public ViewPager pager;
    public PagerAdapter pagerAdapter;
//    public static String summaryString;
    private SummaryFrag summaryFrag;
    private CastListViewFrag castListViewFrag;

    private TextView releaseDate;

//    private Bitmap bitmapPoster;
    private Disposable disposable;
    private Disposable castCallDisposable;


//  todo:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        Intent intent = getIntent();
        String titleFromMain = intent.getStringExtra(RecyclerViewAdapter.TITLE);

        ImageView moviePoster = findViewById(R.id.moviePosterView);
        releaseDate = findViewById(R.id.releaseTextView);
        //    views
        TextView movieTitleView = findViewById(R.id.titleView);
        movieTitleView.setText(titleFromMain);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(intent.getStringExtra(RecyclerViewAdapter.POSTER_PATH))
                .into(moviePoster);

        //        viewPager setup
        pager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        summaryFrag =((ScreenSlidePagerAdapter) pagerAdapter).getSummaryFrag();
        castListViewFrag = ((ScreenSlidePagerAdapter) pagerAdapter).getCastListViewFrag();
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

        String url = "https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + titleFromMain;

        try {
            networkCall(url, "results");


        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        castCallDisposable.dispose();
    }

    void networkCall(final String inputUrl, String jsonKey) {
        disposable = Observable.fromCallable(() -> {
            JSONArray res = getJsonArray(inputUrl, jsonKey);

                if (res.length() > 0) {
                    return res.getJSONObject(0);
                } else {
                    return new JSONObject();
                }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    //            Setting release Date
                    String stringDate = result.getString("release_date").replaceAll("\"","");
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(stringDate);
                    String dateTransf = new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
                    releaseDate.setText(String.format("Coming: %s",  dateTransf));

//              set summary
                    summaryFrag.setSummaryTextViewText(result.getString("overview"));

                    String creditsUrl = "https://api.themoviedb.org/3/movie/" + result.getString("id") + "/credits?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&language=en-US";
                    getCastFromDb(creditsUrl);
                });
    }

    void getCastFromDb(final String inputUrl) {
        castCallDisposable = Observable.fromCallable(() ->  getJsonArray(inputUrl, "cast"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    ArrayList<String> cast = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject json = (JSONObject) result.get(i);
                        cast.add(json.get("name").toString());
                    }

                    castListViewFrag.setCastList(cast);
                });

    }


    private JSONArray getJsonArray(String urlInput, String jsonKey) {
        StringBuilder res = new StringBuilder();
        URL url;
        HttpURLConnection connection;

        try {
            url = new URL(urlInput);

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

            String results = json.getString(jsonKey);

            return new JSONArray(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}

