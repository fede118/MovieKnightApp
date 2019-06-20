package com.example.movieknight;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN ACTIVITY=======>";

//    urls de los estrenos
    private static final String NEW_MOVIES_URL = "https://www.fandango.com/rss/newmovies.rss";
    private static final String COOMING_SOON_MOVIES_URL = "https://www.fandango.com/rss/comingsoonmovies.rss";

//  estrenos de esta semana y los urls de los posters
    public static ArrayList<String> newMovies = new ArrayList<>();

//  proximos estrenos y sus posters
    public static ArrayList<String> comingSoonMovies = new ArrayList<>();

    private static Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView textView = findViewById(R.id.titleTextView);
        textView.setText(Html.fromHtml(title));

        try {
            networkCall(NEW_MOVIES_URL, newMovies, R.id.newMoviesRecycler);
            networkCall(COOMING_SOON_MOVIES_URL, comingSoonMovies, R.id.comingMoviesRecycler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProgressBar progressBar =new ProgressBar(this);
        progressBar.setIndeterminate(true);
    }



    public void networkCall(final String urlInput, final ArrayList<String> movieList, int recyclerId ) {
        disposable = Observable.fromCallable(() -> {
            ArrayList<String> items = new ArrayList<>();
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
                int indexOfTag = s.indexOf("<title><![CDATA[");

                while (indexOfTag > -1) {
                    int indxOfCloseTag = s.indexOf("]]></title>");
                    if (indxOfCloseTag > -1) {
                        String item = s.substring(indexOfTag + 16, indxOfCloseTag);
                        items.add(item);

                        s = s.substring(indxOfCloseTag + 7);
                        indexOfTag =  s.indexOf("<title><![CDATA[");
                    }
                }

                return items;

            } catch (Exception e) {
                e.printStackTrace();
                return items;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    Log.d(TAG, "Rx RESULTS: " + result.toString());

                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                    TextView newMoviesTextView = findViewById(R.id.newMoviesTextView);
                    newMoviesTextView.setVisibility(View.VISIBLE);
                    TextView comingSoonMoviesTextView = findViewById(R.id.comingSoonMoviesTextView);
                    comingSoonMoviesTextView.setVisibility(View.VISIBLE);

                    ArrayList<String> res = formatAllTitles((ArrayList<String>) result);
                    movieList.addAll(res);

                    Log.d(TAG, "initializing reciclerViews");
                    initRecycler(recyclerId, movieList);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

//  =============  HELPERS:
//    inicia el recyclerAdapter a partir del Id, de los titulos y los urls de los Posters
    private void initRecycler (int recyclerId, ArrayList<String> titles ) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(recyclerId);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, titles);
        recyclerView.setAdapter(adapter);
    }

    //  helper remove whitespaces at begginings and ends and remove parenthesis info
    public static String formatTitle(String title) {
        String result = title.replaceAll("\\([^(]*\\)", "");
        result = result.replace("Fandango Early Access:", "");
        result = result.trim();
        return result;
    }

    public ArrayList<String> formatAllTitles (ArrayList<String> list) {
        ArrayList<String> replacement = new ArrayList<>();

        for (String item : list) {
            replacement.add(formatTitle(item));
        }

        return replacement;
    }
}
