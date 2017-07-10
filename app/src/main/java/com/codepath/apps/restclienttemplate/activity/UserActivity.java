package com.codepath.apps.restclienttemplate.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.adapter.UserFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.fragment.NewTweetFragment;
import com.codepath.apps.restclienttemplate.models.LogOnUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    private static String CURRENT_USER_NAME;
    private static String CURRENT_USER_AVATAR;
    private static String CURRENT_LOGINNED_AT;

    private Context mContext;
    private String userId = "";
    private LogOnUser user;

    public String getUserId() {
        return userId;
    }

    private static UserActivity sharedInstance;

    @BindView(R.id.ivProfileAvt)
    ImageView avatar;
    @BindView(R.id.tvProfileUsername)
    TextView username;
    @BindView(R.id.tvProfileAt)
    TextView profileAt;
    @BindView(R.id.tvFollowerCount)
    TextView follower;
    @BindView(R.id.tvFollowingCount)
    TextView following;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static UserActivity newInstance() {
        return sharedInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo);

        mContext = this;
        sharedInstance = this;
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        CURRENT_USER_NAME = intent.getStringExtra("user_avt");
        CURRENT_USER_AVATAR = intent.getStringExtra("user_name");
        getUserInfo(userId);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        UserFragmentPagerAdapter pagerAdapter =
                new UserFragmentPagerAdapter(getSupportFragmentManager(), UserActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case R.id.mnNewTweet:
                showEditDialog();
                return true;
            case R.id.mnProfile:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewTweetFragment newTweetFragment = NewTweetFragment.newInstance(CURRENT_USER_NAME, CURRENT_LOGINNED_AT, CURRENT_USER_AVATAR);
        newTweetFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
        newTweetFragment.show(fm, "fragment_new_tweet");
    }

    private void getUserInfo(String userId) {
        RestClient client = RestApplication.getRestClient();
        client.getUserInformation(userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonUser) {
                user = new LogOnUser(jsonUser);
                CURRENT_USER_NAME = user.getUsername();
                CURRENT_LOGINNED_AT = "@" + user.getAt();
                CURRENT_USER_AVATAR = user.getAvatar();

                username.setText(CURRENT_USER_NAME);
                profileAt.setText(CURRENT_LOGINNED_AT);
                follower.setText(user.getFollower());
                following.setText(user.getFollowing());
                Picasso.with(mContext).load(CURRENT_USER_AVATAR).into(avatar);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jsonObject) {
                Log.d(TAG, t.toString());
            }
        });
    }
}
