package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class Mention {
    private String userAvt;
    private String username;
    private String body;
    private String timestamp;

    public Mention(JSONObject object) {
        try {
            this.username = object.getJSONObject("user").getString("name");
            this.timestamp = getRelativeTimeAgo(object.getString("created_at"));
            this.body = object.getString("text");
            this.userAvt = object.getJSONObject("user").getString("profile_image_url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Mention> fromJson(JSONArray jsonArray) {
        ArrayList<Mention> mentions = new ArrayList<Mention>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject mentionJson = null;
            try {
                mentionJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Mention mention = new Mention(mentionJson);
            mentions.add(mention);
        }

        return mentions;
    }

    public String getUserAvt() {
        return userAvt;
    }

    public void setUserAvt(String userAvt) {
        this.userAvt = userAvt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Mention{" +
                "userAvt='" + userAvt + '\'' +
                ", username='" + username + '\'' +
                ", body='" + body + '\'' +
                '}';
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
}
