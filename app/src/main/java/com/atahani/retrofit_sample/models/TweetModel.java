package com.atahani.retrofit_sample.models;

import java.util.Date;

/**
 * Tweet Model to send new tweet for request body and get in in response
 * NOTE: all of the attr should define as public and also the name should match in REST API
 */
public class TweetModel {
    public String id;
    public String title;
    public String content;
    public String type;
    public String fave_flag;
    public String see_flag;
   // public UserModel user;
}
