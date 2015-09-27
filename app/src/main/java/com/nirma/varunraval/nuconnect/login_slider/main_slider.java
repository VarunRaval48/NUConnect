package com.nirma.varunraval.nuconnect.login_slider;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.nirma.varunraval.nuconnect.R;

public class main_slider extends FragmentActivity {

    private static final int MAX_PAGES = 2;

    private ViewPager mpager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_slider);

        mpager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        mpager.setAdapter(mPagerAdapter);

        //TODO add setOnPageChangeListener

    }

    private class ScreenSlidePageAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePageAdapter(FragmentManager fm){
            super(fm);
        }

        public Fragment getItem(int position){
            return null;
        }

        public int getCount(){
            return MAX_PAGES;
        }
    }
}
