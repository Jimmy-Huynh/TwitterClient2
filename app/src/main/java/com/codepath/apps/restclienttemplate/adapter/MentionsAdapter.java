package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Mention;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class MentionsAdapter extends
        RecyclerView.Adapter<MentionsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Mention> mentions;

    public MentionsAdapter(Context context, ArrayList<Mention> tweets) {
        this.mentions = tweets;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivMentionUserAvt) ImageView userAvatar;
        @BindView(R.id.tvMentionUsername) TextView userName;
        @BindView(R.id.tvMentionBody) TextView tweetBody;
        @BindView(R.id.tvMentionTime) TextView tweetTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void refreshData(ArrayList<Mention> mentions) {
        this.mentions = mentions;
        notifyDataSetChanged();
    }
    public void addData(ArrayList<Mention> mentions) {
        this.mentions.addAll(mentions);
        notifyDataSetChanged();
    }
    public void addDataFirst(Mention mention) {
        this.mentions.add(0, mention);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mentions.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.mention_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Mention tweet = mentions.get(position);

        viewHolder.userName.setText(tweet.getUsername());
        viewHolder.tweetBody.setText(tweet.getBody());

        setTags(viewHolder.tweetBody,tweet.getBody() );

        viewHolder.tweetTime.setText(tweet.getTimestamp());

        loadAvatarToView(tweet.getUserAvt(), viewHolder.userAvatar);



    }

    @Override
    public int getItemCount() {
        return mentions.size();
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

    private void setTags(TextView textView, String pTagString) {
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

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView.setText(string);
    }


}
