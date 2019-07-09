package com.example.movieknight;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SummaryFragment extends Fragment {
    private static final String TAG = "SUMMARY FRAG =====>";

    private TextView summaryTextView;
    private String summaryString = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.summary_layout, container,false);
        summaryTextView = rootView.findViewById(R.id.summaryView);
        summaryTextView.setMovementMethod(new ScrollingMovementMethod());
        summaryTextView.setText(summaryString);

        return rootView;
    }

    public void setSummaryTextViewText(String text) {
        this.summaryString = text;
    }
}
