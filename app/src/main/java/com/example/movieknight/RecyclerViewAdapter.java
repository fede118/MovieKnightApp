package com.example.movieknight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movieknight.model.Movie;
import com.example.movieknight.model.MovieList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RECYCLER ADAPTER ==>";
    //    URLS
    private static final String POSTERS_URL = "https://image.tmdb.org/t/p/w500";


    static final String TITLE = "title";
//    static final String BITMAP_POSTER = "bitmapPoster";
    static final String POSTER_PATH = "posterUrl";

    private MovieList movieList;
    private Context mContext;
    private OnImageClickedListener onImageClickedListener;

    interface OnImageClickedListener {
        void imageClickedOnRecyclerView(Movie movie);
    }

    RecyclerViewAdapter(Context mContext, MovieList movieList, OnImageClickedListener onImageClickedListener) {
        this.movieList = movieList;
        this.mContext = mContext;
        this.onImageClickedListener = onImageClickedListener;
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

        final Movie currentMovie = movieList.result.get(i);

        viewHolder.title.setText(currentMovie.movieTitle);
        viewHolder.progressBar.setVisibility(View.INVISIBLE);
        viewHolder.image.setVisibility(View.VISIBLE);

        if (currentMovie.posterPath == null || currentMovie.posterPath == ""){
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.ic_launcher_foreground)
                    .into(viewHolder.image);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(POSTERS_URL + currentMovie.posterPath)
                    .into(viewHolder.image);
        }

//        TODO: tiene que comunicarse con el presenter y el presenter a la view
//        onClick abrir MovieView activity y mandar el titulo como extra
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickedListener.imageClickedOnRecyclerView(currentMovie);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.result.size();
    }
}
