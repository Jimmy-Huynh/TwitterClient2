package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.codepath.apps.restclienttemplate.fragment.MentionFragment;
import com.codepath.apps.restclienttemplate.fragment.UserFragment;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class UserFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Timeline", "Photos", "Favourites" };
    private Context context;

    public UserFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("position", String.valueOf(position));
        switch (position){
            case 0:
                return UserFragment.newInstance(position + 1);
            case 1:
                return MentionFragment.newInstance(position + 1);
            default:
                return UserFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
