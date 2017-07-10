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
import com.codepath.apps.restclienttemplate.adapter.MentionsAdapter;
import com.codepath.apps.restclienttemplate.models.Mention;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class MentionFragment extends Fragment {

    private static final String STRING_PAGE = "STRING_PAGE";
    private static final String TAG = "MentionFragment";
    private MentionsAdapter mAdapter;
    private Context mContext;
    private ArrayList<Mention> mentions = new ArrayList<>();
    private EndlessRCScrollListener scrollListener;
    int page = 1;
    private SwipeRefreshLayout swipeContainer;


    public static MentionFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(STRING_PAGE, page);
        MentionFragment fragment = new MentionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mention, container, false);

        mContext = this.getContext();

        RecyclerView rcMentions = (RecyclerView) view.findViewById(R.id.rvMentions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rcMentions.setLayoutManager(layoutManager);
        mAdapter = new MentionsAdapter(mContext, mentions);
        rcMentions.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                scrollListener.resetState();
                getMentionTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getMentionTimeline();


        scrollListener = new EndlessRCScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            }
        };
        // Adds the scroll listener to RecyclerView
        rcMentions.addOnScrollListener(scrollListener);
        return view;
    }

    private void getMentionTimeline() {
        RestClient client = RestApplication.getRestClient();
        client.getMentionTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Mention> mentions = Mention.fromJson(jsonArray);
                    mAdapter.refreshData(mentions);
                    Log.d(TAG, jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jsonObject) {
                Log.d(TAG, t.toString());
            }
        });
    }

}
