package com.codepath.apps.restclienttemplate.models;

/**
 * Created by Thieusike on 7/2/2017.
 */
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.apps.restclienttemplate.rest.MyDatabase;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {
    // Define database columns and associated fields
    @PrimaryKey @Column
    Long id;
    @Column
    String userId;
    @Column
    String userHandle;
    @Column
    String userAvt;
    @Column
    String timestamp;
    @Column
    String body;
    @Column
    String imgUrl;
    @Column
    String gif;


    public Tweet(JSONObject object){
        super();

        try {
            this.userId = object.getJSONObject("user").getString("id");
            this.userHandle = object.getJSONObject("user").getString("name");
            this.timestamp = getRelativeTimeAgo(object.getString("created_at"));
            this.body = object.getString("text");
            this.userAvt = object.getJSONObject("user").getString("profile_image_url");

            try {
                JSONObject media = (JSONObject) object.getJSONObject("entities").getJSONArray("media").get(0);
                this.imgUrl = media.getString("media_url");
            }catch (JSONException e){
                this.imgUrl = "noimage";
            }

            try {
                JSONObject extendEntity = (JSONObject) object.getJSONObject("extended_entities").getJSONArray("media").get(0);
                JSONObject urlObject = (JSONObject) extendEntity.getJSONObject("video_info").getJSONArray("variants").get(0);
                this.gif = urlObject.getString("url");
            }catch (JSONException e){
                this.gif = "nogif";
            }

//
//            Log.d("USerid = ", this.userId);
//            Log.d("username = ", this.userHandle);
//            Log.d("createdat = ", this.timestamp);
//            Log.d("body = ", this.body);
//            Log.d("imgUrl = ", this.imgUrl);
//            Log.d("imgUrl = ", this.gif);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Tweet() {

    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
//            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        timestamp = getRelativeTimeAgo(timestamp);
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserAvt() {
        return userAvt;
    }

    public void setUserAvt(String userAvt) {
        this.userAvt = userAvt;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }
}

