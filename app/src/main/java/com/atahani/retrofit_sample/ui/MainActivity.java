package com.atahani.retrofit_sample.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atahani.retrofit_sample.R;
import com.atahani.retrofit_sample.adapter.OperationResultModel;
import com.atahani.retrofit_sample.adapter.DataAdapter;
import com.atahani.retrofit_sample.models.ErrorModel;
import com.atahani.retrofit_sample.models.TweetModel;
import com.atahani.retrofit_sample.network.FakeDataProvider;
import com.atahani.retrofit_sample.network.FakeDataService;
import com.atahani.retrofit_sample.utility.AppPreferenceTools;
import com.atahani.retrofit_sample.utility.Constants;
import com.atahani.retrofit_sample.utility.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DataAdapter mAdapter;
    private FakeDataService mTService;
    private RecyclerView mRylist;
    private AppPreferenceTools mAppPreferenceTools;
    private  Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check is user logged it or not
        mAppPreferenceTools = new AppPreferenceTools(this);
        if (mAppPreferenceTools.isAuthorized()) {
             toolbar = (Toolbar) findViewById(R.id.default_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //bind user image and name to toolbar
          //  txDisplayName.setText(mAppPreferenceTools.getUserName());

            FakeDataProvider provider = new FakeDataProvider();
            mTService = provider.getTService();
            //config recycler view
            mRylist = (RecyclerView) findViewById(R.id.ry_list);
            mRylist.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new DataAdapter(this, new DataAdapter.DataEventHandler() {
                @Override
                public void onEditData(String tweetId, int position) {
                    //start activity to edit tweet
                    Intent editTweetIntent = new Intent(getBaseContext(), CreateOrEditData.class);
                    editTweetIntent.putExtra(Constants.ACTION_TO_DO_KEY, Constants.EDIT_TWEET);
                    editTweetIntent.putExtra(Constants.TWEET_ID_KEY, tweetId);
                    startActivityForResult(editTweetIntent, Constants.CREATE_OR_EDIT_TWEET_REQUEST_CODE);
                }

                @Override
                public void onDeleteData(String tweetId, final int position) {
                    Call<OperationResultModel> call = mTService.deleteTweetById(tweetId);
                    //async request
                    call.enqueue(new Callback<OperationResultModel>() {
                        @Override
                        public void onResponse(Call<OperationResultModel> call, Response<OperationResultModel> response) {
                            if (response.isSuccessful()) {
                                //get tweets from server just for test
                                getTweetsFromServer();
                            } else {
                                ErrorModel errorModel = ErrorUtils.parseError(response);
                                Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<OperationResultModel> call, Throwable t) {
                            //occur when fail to deserialize || no network connection || server unavailable
                            Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            mRylist.setAdapter(mAdapter);
            //get tweets in load
            getTweetsFromServer();
        } else {
            //the user is not logged in so should navigate to sing in activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }

    /**
     * get tweets from server
     */
    private void getTweetsFromServer() {
        Call<List<TweetModel>> call = mTService.getTweets();
        call.enqueue(new Callback<List<TweetModel>>() {
            @Override
            public void onResponse(Call<List<TweetModel>> call, Response<List<TweetModel>> response) {

                if (response.isSuccessful()) {
                    //update the adapter data
                    mAdapter.updateAdapterData(response.body());
                    mAdapter.notifyDataSetChanged();
                } else {
                    ErrorModel errorModel = ErrorUtils.parseError(response);
                    Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TweetModel>> call, Throwable t) {
                //occur when fail to deserialize || no network connection || server unavailable
                Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.CREATE_OR_EDIT_TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            getTweetsFromServer();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            EditText txsearch = (EditText) toolbar.findViewById(R.id.Etsearch);
           // txsearch.setVisibility(View.VISIBLE);

        }

        if (id == R.id.action_add) {
            //start new activity to send tweet
            Intent postNewTweetIntent = new Intent(this, CreateOrEditData.class);
            postNewTweetIntent.putExtra(Constants.ACTION_TO_DO_KEY, Constants.NEW_TWEET);
            startActivityForResult(postNewTweetIntent, Constants.CREATE_OR_EDIT_TWEET_REQUEST_CODE);
        } else if (id == R.id.action_log_out) {

            //send request to server to terminate this application
            Call<OperationResultModel> call = mTService.terminateApp(mAppPreferenceTools.getAccessToken());
            call.enqueue(new Callback<OperationResultModel>() {
                @Override
                public void onResponse(Call<OperationResultModel> call, Response<OperationResultModel> response) {
                    if (response.isSuccessful()) {
                        //remove all authentication information such as accessToken and others
                        mAppPreferenceTools.removeAllPrefs();
                        //navigate to sign in activity
                        startActivity(new Intent(getBaseContext(), SignInActivity.class));
                        //finish this
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<OperationResultModel> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
