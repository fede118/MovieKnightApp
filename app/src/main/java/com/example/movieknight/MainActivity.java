package com.example.movieknight;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

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
    public static ArrayList<String> newMoviesImageUrls = new ArrayList<>();
//  proximos estrenos y sus posters
    public static ArrayList<String> comingSoonMovies = new ArrayList<>();
    public static ArrayList<String> comingSoonImageUrls = new ArrayList<>();

    private static Disposable observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(title));

        try {
            networkCall(NEW_MOVIES_URL, newMovies, newMoviesImageUrls, R.id.newMoviesRecycler);
            networkCall(COOMING_SOON_MOVIES_URL, comingSoonMovies, comingSoonImageUrls, R.id.comingMoviesRecycler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void networkCall(final String urlInput, final ArrayList<String> movieList, ArrayList<String> imageUrlList, int recyclerId ) {
        observable = Observable.fromCallable(() -> {
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
                    ArrayList<String> res = formatAllTitles((ArrayList<String>) result);
                    movieList.addAll(res);
                    getImagesUrl(movieList, imageUrlList);
                    initRecycler(recyclerId, movieList, imageUrlList);
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        observable.dispose();
    }

//  =============  HELPERS:
//    inicia el recyclerAdapter a partir del Id, de los titulos y los urls de los Posters
    private void initRecycler (int recyclerId, ArrayList<String> titles, ArrayList<String> imgUrls ) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(recyclerId);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, titles, imgUrls);
        recyclerView.setAdapter(adapter);
    }

//    obtiene el poster path para todos los titulos y lo guarda en la lista
    public void getImagesUrl(ArrayList<String> movieList, ArrayList<String> imgUrls) {
        for (String item: movieList) {
            item = formatTitle(item);
            String posterPath = "none";
            try {
                posterPath = new getPosterPath().execute("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + item + "&callback=?").get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgUrls.add(posterPath);
        }
    }

//    helper GetJson
    public static class getPosterPath extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
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
                    JSONObject firstResult = jsonArray.getJSONObject(0);

    //                    setting poster
                    return firstResult.getString("poster_path");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "none";
        }
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
