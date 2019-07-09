package com.example.movieknight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    private SummaryFragment summaryFrag;
    private CastListViewFragment castListViewFrag;


    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        this.summaryFrag = new SummaryFragment();
        this.castListViewFrag = new CastListViewFragment();
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

    public SummaryFragment getSummaryFrag() {
        return summaryFrag;
    }
    public CastListViewFragment getCastListViewFrag() {
        return castListViewFrag;
    }
}
