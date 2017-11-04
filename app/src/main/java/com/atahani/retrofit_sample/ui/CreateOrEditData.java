package com.atahani.retrofit_sample.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.atahani.retrofit_sample.R;
import com.atahani.retrofit_sample.models.ErrorModel;
import com.atahani.retrofit_sample.models.TweetModel;
import com.atahani.retrofit_sample.network.FakeDataProvider;
import com.atahani.retrofit_sample.network.FakeDataService;
import com.atahani.retrofit_sample.utility.Constants;
import com.atahani.retrofit_sample.utility.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrEditData extends AppCompatActivity {

    private AppCompatEditText mETxTweetBody;

    private int mActionToDo = Constants.NEW_TWEET;
    private String mTweetIdInEditMode;
    private FakeDataService mTService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_data);
        //get argument and check is in edit mode
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mActionToDo = args.getInt(Constants.ACTION_TO_DO_KEY, Constants.NEW_TWEET);
            if (mActionToDo == Constants.EDIT_TWEET) {
                mTweetIdInEditMode = args.getString(Constants.TWEET_ID_KEY, "");
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mETxTweetBody = (AppCompatEditText) findViewById(R.id.etx_tweet_body);
        //the default mode is happy so we selected

        //first create new instant of FakeDataProvider
        FakeDataProvider provider = new FakeDataProvider();
        //get the FakeDataService interface to call API routes
        mTService = provider.getTService();

        //check if in edit mode get tweet information from server and assign in
        //NOTE: this is just for test , in real world should save tweet in db and now get from db !
        if (mActionToDo == Constants.EDIT_TWEET && !mTweetIdInEditMode.equals("")) {
            //get tweet by id from server
            Call<TweetModel> call = mTService.getTweetById(mTweetIdInEditMode);
            call.enqueue(new Callback<TweetModel>() {
                @Override
                public void onResponse(Call<TweetModel> call, Response<TweetModel> response) {
                    if (response.isSuccessful()) {
                        //bind value to fields
                        mETxTweetBody.setText(response.body().title);
                        //get code point of emoji

                    } else {
                        ErrorModel errorModel = ErrorUtils.parseError(response);
                        Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TweetModel> call, Throwable t) {
                    //occur when fail to deserialize || no network connection || server unavailable
                    Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mActionToDo == Constants.EDIT_TWEET) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_send, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {
            //Send tweet
            //create Tweet Model
            TweetModel tweetModel = new TweetModel();
            //assign tweet model values
            tweetModel.title = mETxTweetBody.getText().toString();

            //create call generic class to send request to server
            Call<TweetModel> call = mTService.createNewTweet(tweetModel);
            //Async request
            //NOTE: you should always send Async request since the sync request cause crash in your application
            call.enqueue(new Callback<TweetModel>() {
                @Override
                public void onResponse(Call<TweetModel> call, Response<TweetModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Successfully post new tweet", Toast.LENGTH_LONG).show();
                        //finish this activity with result OK to refresh the data from server
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ErrorModel errorModel = ErrorUtils.parseError(response);
                        Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TweetModel> call, Throwable t) {
                    //occur when fail to deserialize || no network connection || server unavailable
                    Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == R.id.action_edit) {
            //edit this tweet
            TweetModel tweetModel = new TweetModel();
            //assign tweet model values
            tweetModel.title = mETxTweetBody.getText().toString();

            Call<TweetModel> call = mTService.updateTweetById(mTweetIdInEditMode, tweetModel);
            call.enqueue(new Callback<TweetModel>() {
                @Override
                public void onResponse(Call<TweetModel> call, Response<TweetModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Successfully updated", Toast.LENGTH_LONG).show();
                        //finish this activity with result OK to refresh the data from server
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ErrorModel errorModel = ErrorUtils.parseError(response);
                        Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TweetModel> call, Throwable t) {
                    //occur when fail to deserialize || no network connection || server unavailable
                    Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == android.R.id.home) {
            //back to main activity
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * get String from emoji hex unicode
     *
     * @param emojiCode
     * @return
     */
    private String getStringOfEmojiCode(int emojiCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(emojiCode));
        return sb.toString();
    }


    }

