package com.example.movieknight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    private SummaryFrag summaryFrag;
    private CastListViewFrag castListViewFrag;


    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        this.summaryFrag = new SummaryFrag();
        this.castListViewFrag = new CastListViewFrag();
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return summaryFrag;
            case 1:
                return castListViewFrag;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    public SummaryFrag getSummaryFrag() {
        return summaryFrag;
    }
    public CastListViewFrag getCastListViewFrag() {
        return castListViewFrag;
    }
}
