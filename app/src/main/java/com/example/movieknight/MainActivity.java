package com.example.movieknight;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    String TAG = "MAIN ACTIVITY ==========>";
//  estrenos de esta semana y los urls de los posters
    public static ArrayList<String> newMovies = new ArrayList<>();
    public static ArrayList<String> imageUrls = new ArrayList<>();
//  proximos estrenos y sus posters
    public static ArrayList<String> comingSoonMovies = new ArrayList<>();
    public static ArrayList<String> comingImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(title));

        try {
            newMovies = new getRss().execute("https://www.fandango.com/rss/newmovies.rss").get();
            comingSoonMovies = new getRss().execute("https://www.fandango.com/rss/comingsoonmovies.rss").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        newMovies = formatAllTitles(newMovies);
        comingSoonMovies = formatAllTitles(comingSoonMovies);

        getImagesUrl(newMovies, imageUrls);
        getImagesUrl(comingSoonMovies,comingImageUrls);

        initRecycler(R.id.newMoviesRecycler, newMovies, imageUrls);
        initRecycler(R.id.comingMoviesRecycler, comingSoonMovies, comingImageUrls);
    }

//    obtiene rss feed para usar en Fandango.com para obtener los estrenos de esta semana y los proximos estrenos
    public static class getRss extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            ArrayList<String> items = new ArrayList<>();
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
        }
    }

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
    public String formatTitle(String title) {
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
