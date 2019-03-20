package com.example.movieknight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    ArrayList<String> newMovies = new ArrayList<>();
    ArrayList<String> comingSoonMovies = new ArrayList<>();
    ListView listViewNewMovies;
    ListView listViewComingSoon;
    ArrayAdapter adapterNewMovies;
    ArrayAdapter adapterComingSoon;

    int executionN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(title));

        listViewNewMovies = findViewById(R.id.listView);
        adapterNewMovies = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newMovies);
        listViewNewMovies.setAdapter(adapterNewMovies);

        listViewComingSoon = findViewById(R.id.listView2);
        adapterComingSoon = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,comingSoonMovies);
        listViewComingSoon.setAdapter(adapterComingSoon);

        try {
            String response = new getRss().execute("https://www.fandango.com/rss/newmovies.rss").get();
            System.out.println("RESPONSE =======>" + response);
            String res = new getRss().execute("https://www.fandango.com/rss/comingsoonmovies.rss").get();
            System.out.println("RES =======>" + res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listViewNewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    Intent intent = new Intent(MainActivity.this, MovieViewActivity.class);
                    intent.putExtra("title",newMovies.get(position));
                    startActivity(intent);
                }
            }
        });
    }

    public class getRss extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return doInBackgroundHelper(urls);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            executionN++;

            if (executionN == 1) {
                onPostExecuteHelper(s, newMovies);
                adapterNewMovies.notifyDataSetChanged();
            } else if (executionN == 2) {
                onPostExecuteHelper(s, comingSoonMovies);
                adapterComingSoon.notifyDataSetChanged();
            }
        }
    }

// helper
    public static String doInBackgroundHelper (String... urls) {
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

            return res.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "FAILED";
        }
    }
    public void onPostExecuteHelper (String s, ArrayList<String> list) {
        int indexOfTag = s.indexOf("<title><![CDATA[");

        while (indexOfTag > -1) {
            int indxOfCloseTag = s.indexOf("]]></title>");
            if (indxOfCloseTag > -1) {
                String item = s.substring(indexOfTag + 16, indxOfCloseTag);
                if (!item.equals("New Movies")) {
                    list.add(item);
                }
                s = s.substring(indxOfCloseTag + 7);
                indexOfTag =  s.indexOf("<title><![CDATA[");
            }
        }
    }
}
