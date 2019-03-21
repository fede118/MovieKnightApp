package com.example.movieknight;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mNames;
    private ArrayList<String> mImageUrls;
    private Context mContext;

    private RecyclerViewAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mImageUrls) {
        this.mNames = mNames;
        this.mImageUrls = mImageUrls;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (mImageUrls.get(i).equals("none")){
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.ic_launcher_foreground)
                    .into(viewHolder.image);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load("https://image.tmdb.org/t/p/w500" + mImageUrls.get(i))
                    .into(viewHolder.image);
        }

        viewHolder.releaseDate.setText(formatTitle(mNames.get(i)));

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                System.out.println("title ============>" + viewHolder.releaseDate.getText().toString());
                intent.putExtra("title", viewHolder.releaseDate.getText().toString());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView releaseDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            releaseDate = itemView.findViewById(R.id.releaseTextView);
        }
    }

    private String formatTitle(String title) {
        String result = title.replaceAll("\\([^(]*\\)", "");
        result = result.trim();
        result = result.replace("Fandango Early Access:", "");
        return result;
    }
}
