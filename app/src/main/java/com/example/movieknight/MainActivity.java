package com.example.movieknight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    String TAG = "MAIN ACTIVITY ==========>";
    
    public static ArrayList<String> newMovies = new ArrayList<>();
    public static ArrayList<String> imageUrls = new ArrayList<>();

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

        getImages(newMovies, imageUrls);
        getImages(comingSoonMovies,comingImageUrls);
        initNewMoviesRecycler();
        initComingMoviesRecycler();
    }

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

//    recyclers initializer
    private void initNewMoviesRecycler () {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.newMoviesRecycler);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, newMovies, imageUrls);
        recyclerView.setAdapter(adapter);
    }
    private void initComingMoviesRecycler () {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.comingMoviesRecycler);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, comingSoonMovies, comingImageUrls);
        recyclerView.setAdapter(adapter);
    }

    public void getImages(ArrayList<String> movieList, ArrayList<String> imgUrls) {
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
