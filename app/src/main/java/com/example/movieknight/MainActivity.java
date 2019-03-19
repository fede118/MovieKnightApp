package com.example.movieknight;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Xml;
import android.view.View;
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
    ArrayList<String> items = new ArrayList<>();
    ListView listView;
    ArrayAdapter adapter;
    int indexOfCs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = "<font color='#4286F4'>M</font>ovie<font color='#4286F4'>K</font>night";
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(title));


        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        try {
            String response = new getRss().execute("https://www.fandango.com/rss/newmovies.rss").get();
            String res = new getRss().execute("https://www.fandango.com/rss/comingsoonmovies.rss").get();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        getRss.Status.values().equals(AsyncTask.Status.FINISHED)
//                System.out.println("STATUS ============> FINISHED");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0){
                    if (position != indexOfCs) {
                        Intent intent = new Intent(MainActivity.this, MovieViewActivity.class);
                        intent.putExtra("title",items.get(position));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public class getRss extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (items.size() == 0) {
                items.add("This Week:");
            }
            return doInBackgroundHelper(urls);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
             if (items.size() > 1 && !items.contains("Coming Soon:")) {

                items.add("Coming Soon:");
                indexOfCs = items.size() - 1;
            }

            int indexOfTag = s.indexOf("<title><![CDATA[");

            while (indexOfTag > -1) {
                int indxOfCloseTag = s.indexOf("]]></title>");
                if (indxOfCloseTag > -1) {
                    String item = s.substring(indexOfTag + 16, indxOfCloseTag);
                    if (!item.equals("New Movies")) {
                        items.add(item);
                    }
                    s = s.substring(indxOfCloseTag + 7);
                    indexOfTag =  s.indexOf("<title><![CDATA[");
                }
            }

            setTitlesOnListView();
            System.out.println(Arrays.toString(getRss.Status.values()));
            System.out.println("CHILD COUNT ===========> " + listView.getChildCount());
            adapter.notifyDataSetChanged();
            System.out.println(Arrays.toString(getRss.Status.values()));
            System.out.println("CHILD COUNT ===========> " + listView.getChildCount());
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

    public void setTitlesOnListView () {
        for (int i = 0; i < listView.getChildCount(); i++) {
            TextView text = (TextView) listView.getChildAt(i);
            System.out.println("ITEM AT " + i + ": ======> " + text.getText().toString());
        }
        TextView newMovies = (TextView) adapter.getView(0, null, listView);
        newMovies.setText(Html.fromHtml("<b>This Week:</b>"));
        newMovies.setTextSize(35);

        TextView comingSoon =(TextView) adapter.getView(indexOfCs, null, listView);
        comingSoon.setText(Html.fromHtml("<b>coming Soon:</b>"));
        comingSoon.setTextSize(35);

        adapter.notifyDataSetChanged();
    }
}
