package com.atahani.retrofit_sample.utility;

/**
 * contain client information such as BASE_URL || client information
 */
public class ClientConfigs {
    //TODO: should get network ip address like 192.168.1.2 and replace inside REST_API_BASE_URL
    public static final String REST_API_BASE_URL = "http://ictplus.ir/public/";
    //TODO: create new Client with postman in http://localshot:/api/v1/client with body {"name":"android client app"} and set these values with client_id and client_key
    public static final String CLIENT_ID = "390e6431-033b-32e0-a674-ace20462ec0c";
    public static final String CLIENT_KEY = "11";
}