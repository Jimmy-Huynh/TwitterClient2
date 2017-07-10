package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class LogOnUser {
    private String userId;
    private String username;
    private String follower;
    private String following;
    private String avatar;
    private String at;



    public LogOnUser(JSONObject object){
        super();

        try {
            this.userId = object.getString("id");
            this.username = object.getString("name");
            this.follower = object.getString("followers_count");
            this.following = object.getString("friends_count");
            this.avatar = object.getString("profile_image_url");
            this.at = object.getString("screen_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "LogOnUser{" +
                "username='" + username + '\'' +
                ", follower='" + follower + '\'' +
                ", following='" + following + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public LogOnUser(String username, String follower, String following) {
        this.username = username;
        this.follower = follower;
        this.following = following;
    }

    public LogOnUser() {

    }
}
