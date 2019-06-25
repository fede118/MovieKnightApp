package com.example.movieknight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RECYCLER ADAPTER ==>";

    static final String TITLE = "title";
//    static final String BITMAP_POSTER = "bitmapPoster";
    static final String POSTER_PATH = "posterUrl";
    //    URLS
    private static final String POSTERS_URL = "https://image.tmdb.org/t/p/w500";

    private ArrayList<String> mNames;
    private String posterPath;
    private Context mContext;
    private Disposable imageDisposable;

    RecyclerViewAdapter(Context mContext, ArrayList<String> mNames) {
        this.mNames = mNames;
        this.mContext = mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.appTitleTextView);
            progressBar = itemView.findViewById(R.id.imageItemProgressBar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_listitem,viewGroup,false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
//        todo: si mNames esta vacia deberia pasar algo como que diga que no hay peliculas

        //        setting title
        viewHolder.title.setText(MainActivity.formatTitle(mNames.get(i)));

        //        setting poster
        getPosterPath("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + mNames.get(i), viewHolder);

//        onClick abrir MovieView activity y mandar el titulo como extra
        viewHolder.image.setOnClickListener(v -> {
            posterPath = viewHolder.title.getTag().toString();

            Intent intent = new Intent(mContext, MovieViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(TITLE, viewHolder.title.getText().toString());
            intent.putExtra(POSTER_PATH, posterPath);

            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        imageDisposable.dispose();
    }

    private void getPosterPath(final String imgUrl, final ViewHolder viewHolder) {
        imageDisposable = Observable.fromCallable(() -> {
            StringBuilder res = new StringBuilder();
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(imgUrl);

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

                JSONObject json = new JSONObject(s);

                String results = json.getString("results");

                JSONArray jsonArray = new JSONArray(results);

                if (jsonArray.length() > 0) {
                    JSONObject firstResult = jsonArray.getJSONObject(0);

                    //                    setting poster
                    return firstResult.getString("poster_path");
                } else {
                    return "none";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {

                    viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    viewHolder.image.setVisibility(View.VISIBLE);


                    if (result.equals("none")){
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_launcher_foreground)
                                .into(viewHolder.image);
                    } else {
                        viewHolder.title.setTag(POSTERS_URL + result);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(POSTERS_URL + result)
                                .into(viewHolder.image);
                    }
                });
    }
}
