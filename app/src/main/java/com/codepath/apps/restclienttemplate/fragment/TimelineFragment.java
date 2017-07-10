package com.codepath.apps.restclienttemplate.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.adapter.EndlessRCScrollListener;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.LogOnUser;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class TimelineFragment extends Fragment implements NewTweetFragment.NewTweetFragmentListener {

    public static final String STRING_PAGE = "STRING_PAGE";
    private static final String TAG = "TimelineFragment";

    private RecyclerView rcTweets;
    private TweetAdapter mAdapter;
    private Context mContext;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EndlessRCScrollListener scrollListener;
    private int page = 1;
    private SwipeRefreshLayout swipeContainer;
    private static TimelineFragment sharedInstance;
    private LogOnUser user;

    public static TimelineFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(STRING_PAGE, page);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        sharedInstance = fragment;
        return sharedInstance;
    }

    public TweetAdapter getmAdapter() {
        return mAdapter;
    }

    public static TimelineFragment getSharedInstance() {
        return sharedInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        mContext = this.getContext();

        rcTweets = (RecyclerView) view.findViewById(R.id.rvTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rcTweets.setLayoutManager(layoutManager);
        mAdapter = new TweetAdapter(mContext, tweets);
        rcTweets.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                scrollListener.resetState();
                updateHomeTimeline(1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getHomeLine(page);
        getCurrentLoggedOnUser();


        scrollListener = new EndlessRCScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rcTweets.addOnScrollListener(scrollListener);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        page = offset;
        getHomeLine(offset);
        System.out.println("offset" + offset);
    }

    private void getHomeLine(int page) {
        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    mAdapter.addData(tweets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jsonObject) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void updateHomeTimeline(int page) {
        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    mAdapter.refreshData(tweets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jsonObject) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void getCurrentLoggedOnUser() {
        RestClient client = RestApplication.getRestClient();
        client.getCurrentLoggedOnUser(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonUser) {
                user = new LogOnUser(jsonUser);
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
        tweet.setId((long) Math.random() * 212 * ((long) Math.random() * 232 * ((long) Math.random() * 121)));
        tweet.setUserAvt(user.getAvatar());
        tweet.setUserHandle(user.getUsername());
        tweet.setGif("nogif");
        tweet.setImgUrl("noimage");
        mAdapter.addDataFirst(tweet);

    }

}
