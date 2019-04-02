package com.example.movieknight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.TextView;

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    SummaryFrag summaryFrag = new SummaryFrag();

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SummaryFrag();
            case 1:
                return new SecondViewPagerFrag();
            default:
                return new SummaryFrag();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}
