package com.example.movieknight;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN ACTIVITY=======>";

//    urls de los estrenos
    private static final String NEW_MOVIES_URL = "https://www.fandango.com/rss/newmovies.rss";
    private static final String COMING_SOON_MOVIES_URL = "https://www.fandango.com/rss/comingsoonmovies.rss";

//  estrenos de esta semana
    public ArrayList<String> newMovies = new ArrayList<>();

//  proximos estrenos (semana posterior en adelante)
    public ArrayList<String> comingSoonMovies = new ArrayList<>();

    private static Disposable disposable;

    private ProgressBar progressBar;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView appTitleTextView = findViewById(R.id.appTitleTextView);
        appTitleTextView.setText(Html.fromHtml(title));

        try {
            networkCall(NEW_MOVIES_URL, newMovies, R.id.newMoviesRecycler);
            networkCall(COMING_SOON_MOVIES_URL, comingSoonMovies, R.id.comingMoviesRecycler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBar = new ProgressBar(this);
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

            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
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

                    if (result.toString() == "false") {
                        Toast.makeText(this, "Couldn't comunicate with the server! :(", Toast.LENGTH_SHORT).show();
                        createRetryButton();
                        return;
                    }

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

//    create button to retry conection with the servers
    private void createRetryButton() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);

        retryButton = new Button(this);
        retryButton.setId(View.generateViewId());
        int buttonId = retryButton.getId();
        retryButton.setText(R.string.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                try {
                    networkCall(NEW_MOVIES_URL, newMovies, R.id.newMoviesRecycler);
                    networkCall(COMING_SOON_MOVIES_URL, comingSoonMovies, R.id.comingMoviesRecycler);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                retryButton.setVisibility(View.INVISIBLE);
                ((ViewManager) retryButton.getParent()).removeView(retryButton);
            }
        });
        layout.addView(retryButton,0);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(buttonId, ConstraintSet.TOP, R.id.appTitleTextView,ConstraintSet.BOTTOM, 32);
        set.connect(buttonId, ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        set.connect(buttonId, ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
        set.applyTo(layout);
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
