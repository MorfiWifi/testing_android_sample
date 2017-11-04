package com.atahani.retrofit_sample.network;

import com.atahani.retrofit_sample.adapter.OperationResultModel;
import com.atahani.retrofit_sample.models.AuthenticationResponseModel;
import com.atahani.retrofit_sample.models.SignInRequestModel;
import com.atahani.retrofit_sample.models.SignUpRequestModel;
import com.atahani.retrofit_sample.models.TweetModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * the interface implements REST API routes
 */
public interface FakeDataService {

    @POST("tweet")
    Call<TweetModel> createNewTweet(@Body TweetModel tweetModel);

    @GET("book")
    Call<List<TweetModel>> getTweets();

    @GET("tweet/{id}")
    Call<TweetModel> getTweetById(@Path("id") String tweetId);

    @PUT("tweet/{id}")
    Call<TweetModel> updateTweetById(@Path("id") String tweetId, @Body TweetModel tweetModel);

    @DELETE("tweet/{id}")
    Call<OperationResultModel> deleteTweetById(@Path("id") String tweetId);

    @POST("signup")
    Call<AuthenticationResponseModel> signUp(@Body SignUpRequestModel signUpRequestModel);

    @POST("signin")
    Call<AuthenticationResponseModel> signIn(@Body SignInRequestModel signInRequestModel);

    @POST("user/app")
    Call<OperationResultModel> terminateApp(@Header("Authorization") String authHeader);

}
