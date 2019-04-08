package com.example.movieknight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CastListViewFrag extends Fragment {
    private static final String TAG = "SECOND VIEW PAGER FRAG";
    
    public ArrayList<String> castList;
    ListView castListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.second_viewpager_layout, container, false);

        castListView = rootView.findViewById(R.id.castListView);

        castList = demoList(new ArrayList<String>(), 10);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, castList);
        castListView.setAdapter(adapter);

        return rootView;
    }

//    TODO: investigar si puedo tener el cast
//    TODO: llenar la lista con el cast verdadero
    public ArrayList<String> demoList (ArrayList<String> arrayList, int size) {
        for (int i = 0; i < size; i++) {
            arrayList.add("Fede" + String.valueOf(i));
        }
        return arrayList;
    }
}
