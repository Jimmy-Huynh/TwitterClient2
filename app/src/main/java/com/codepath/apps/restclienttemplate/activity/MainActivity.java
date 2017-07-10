package com.codepath.apps.restclienttemplate.activity;

/**
 * Created by Thieusike on 7/2/2017.
 */

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.fragment.NewTweetFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.adapter.TimelineFragmentAdapter;
import com.codepath.apps.restclienttemplate.fragment.TimelineFragment;
import com.codepath.apps.restclienttemplate.models.LogOnUser;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements NewTweetFragment.NewTweetFragmentListener {
    private static final String TAG = "MainActivity";
    private static String CURRENT_LOGON_USERNAME;
    private static String CURRENT_LOGON_USERAVT;
    private static String CURRENT_LOGON_AT;
    private LogOnUser mLogOnUser;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        TimelineFragmentAdapter pagerAdapter =
                new TimelineFragmentAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        getCurrentLoggedOnUser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case R.id.mnNewTweet:
                showEditDialog();
                return true;
            case R.id.mnProfile:
                Intent i = new Intent(this, UserActivity.class);
                i.putExtra("user_id", mLogOnUser.getUserId());
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewTweetFragment newTweetFragment = NewTweetFragment.newInstance(CURRENT_LOGON_USERNAME, CURRENT_LOGON_AT, CURRENT_LOGON_USERAVT);
        newTweetFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
        newTweetFragment.show(fm, "fragment_new_tweet");
    }


    private void getCurrentLoggedOnUser() {
        RestClient client = RestApplication.getRestClient();
        client.getCurrentLoggedOnUser(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonUser) {
                mLogOnUser = new LogOnUser(jsonUser);
                CURRENT_LOGON_USERNAME = mLogOnUser.getUsername();
                CURRENT_LOGON_AT = mLogOnUser.getAt();
                CURRENT_LOGON_USERAVT = mLogOnUser.getAvatar();
            }

            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jsonObject) {
                Log.d(TAG, t.toString());
            }
        });
    }


    @Override
    public void onFinishNewTweetFragmentDialog(String body, String time) {
        Tweet tweet = new Tweet();
        tweet.setBody(body);
        tweet.setTimestamp(time);
        tweet.setId((long) (Math.random() * 212 * ((int) (Math.random() * 232 * ((long) Math.random() * 121)))));
        tweet.setUserAvt(CURRENT_LOGON_USERAVT);
        tweet.setUserHandle(CURRENT_LOGON_USERNAME);
        tweet.setGif("nogif");
        tweet.setImgUrl("noimage");
        TimelineFragment.getSharedInstance().getmAdapter().addDataFirst(tweet);

    }
}
