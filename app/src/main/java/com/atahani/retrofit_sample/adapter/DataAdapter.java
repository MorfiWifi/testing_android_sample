package com.atahani.retrofit_sample.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atahani.retrofit_sample.R;
import com.atahani.retrofit_sample.models.TweetModel;
import com.atahani.retrofit_sample.utility.AppPreferenceTools;

import java.util.Collections;
import java.util.List;

/**
 *  Adapter show tweet in Recycler view
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<TweetModel> mData = Collections.emptyList();
    private DataEventHandler mDataEventHandler;
    private AppPreferenceTools mAppPreferenceTools;

    public DataAdapter(Context context, DataEventHandler dataEventHandler) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataEventHandler = dataEventHandler;
        mAppPreferenceTools = new AppPreferenceTools(this.mContext);
    }

    public void updateAdapterData(List<TweetModel> data) {
        this.mData = data;
    }


    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.data_row, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        TweetModel currentModel = mData.get(position);
        holder.mTxTweetBody.setText(currentModel.title);
        //convert char string into Hex code point


      //  if (currentModel.user.id.equals(mAppPreferenceTools.getUserId())) {
        //  holder.mLyAction.setVisibility(View.VISIBLE);
        //    holder.mTxUserDisplayName.setText(mAppPreferenceTools.getUserName());

        //} else {
          //  holder.mTxUserDisplayName.setText(currentModel.user.name);

//            holder.mLyAction.setVisibility(View.GONE);
  //      }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * view holder for tweet adapter we have one view as data_rowxml layout
     */
    public class DataViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLyAction;
        private AppCompatTextView mTxTweetBody;
        private AppCompatImageButton mImEdit;
        private AppCompatImageButton mImDelete;


        public DataViewHolder(View itemView) {
            super(itemView);
            mLyAction = (LinearLayout) itemView.findViewById(R.id.ly_action);
            mTxTweetBody = (AppCompatTextView) itemView.findViewById(R.id.tx_tweet_body);
            mImEdit = (AppCompatImageButton) itemView.findViewById(R.id.im_edit);
            mImDelete = (AppCompatImageButton) itemView.findViewById(R.id.im_delete);
            mImDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataEventHandler != null) {
                        mDataEventHandler.onDeleteData(mData.get(getAdapterPosition()).id, getAdapterPosition());
                    }
                }
            });

            mImEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataEventHandler != null) {
                        mDataEventHandler.onEditData(mData.get(getAdapterPosition()).id, getAdapterPosition());
                    }
                }
            });
        }
    }


    /**
     * define interface to handle events
     */
    public interface DataEventHandler {
        void onEditData(String tweetId, int position);

        void onDeleteData(String tweetId, int position);
    }
}
