package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activity.UserActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Tweet> tweets;

    public TweetAdapter(Context context, ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ivUserAvt) ImageView userAvatar;
        @BindView(R.id.tvUsername) TextView userName;
        @BindView(R.id.tvTweetBody) TextView tweetBody;
        @BindView(R.id.tvTweetTime) TextView tweetTime;
        @BindView(R.id.ivTweetImage) ImageView tweetImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            userAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = tweets.get(position);
                // We can access the data within the views
                Intent i = new Intent(mContext, UserActivity.class);
                i.putExtra("user_id", tweet.getUserId());
                i.putExtra("user_avt", tweet.getUserAvt());
                i.putExtra("user_name", tweet.getUserHandle());

                mContext.startActivity(i);
            }
        }
    }

    public void refreshData(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        notifyDataSetChanged();
    }
    public void addData(ArrayList<Tweet> tweets) {
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }
    public void addDataFirst(Tweet tweet) {
        this.tweets.add(0, tweet);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.tweets.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.tweet_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tweet tweet = tweets.get(position);

        viewHolder.userName.setText(tweet.getUserHandle());
        viewHolder.tweetBody.setText(tweet.getBody());

        setTags(viewHolder.tweetBody,tweet.getBody() );
        Linkify.addLinks(viewHolder.tweetBody, Linkify.WEB_URLS);
        viewHolder.tweetTime.setText(tweet.getTimestamp());



        if (tweet.getImgUrl().equals("noimage")){
            viewHolder.tweetImage.setVisibility(View.GONE);

        }else {
            viewHolder.tweetImage.setVisibility(View.VISIBLE);
            loadImageToView(tweet.getImgUrl(), viewHolder.tweetImage);
        }



        loadAvatarToView(tweet.getUserAvt(), viewHolder.userAvatar);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    private void loadImageToView(String imageUrl, ImageView view){

        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .into(view);

    }

    private void loadAvatarToView(String imageUrl, ImageView view){
        Glide.with(mContext).load(imageUrl).asBitmap().skipMemoryCache(true).centerCrop().into(new BitmapImageViewTarget(view) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    private void loadGifToView(String imageUrl, ImageView view){
        Glide.with(mContext)
                .load(imageUrl )
                .thumbnail(0.1f)
                .into(view);
    }


    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#' ) {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#33b5e5"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());

        pTextView.setText(string);
    }


}
