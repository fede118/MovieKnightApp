package com.example.movieknight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SummaryFrag extends Fragment {
    private static final String TAG = "SUMMARY FRAG =====>";

    TextView summaryTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.summary_layout, container,false);
        summaryTextView = rootView.findViewById(R.id.summaryView);
        if (MovieViewActivity.summaryString != null) {
            summaryTextView.setText(MovieViewActivity.summaryString);
            summaryTextView.setMovementMethod(new ScrollingMovementMethod());
        }

        return rootView;
    }
}
