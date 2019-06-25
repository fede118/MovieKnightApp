package com.example.movieknight;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class CastListViewFrag extends Fragment {
    private static final String TAG = "SECOND VIEW PAGER FRAG";

    public ArrayList<String> castList;
    private ListView castListView;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.second_viewpager_layout, container, false);
        castListView = rootView.findViewById(R.id.castListView);

        context = rootView.getContext();

        castList = new ArrayList<>();

        return rootView;
    }

    void setCastList(ArrayList<String>  list) {
        this.castList = list;
        if (castList.size() == 0) {
            castList.add("Couldn't find cast for this movie, sorry");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, castList);
        castListView.setAdapter(adapter);
    }
}
