package com.example.frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.AccountActivity;
import com.example.frontend.AccountFragment;
import com.example.frontend.R;
import com.example.frontend.SettingPasswordActivity;
import com.example.frontend.info.PostInfo;
import com.example.frontend.info.RelationInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

public class PostAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int ITEM_TYPE0 = 0;
    private int ITEM_TYPE1 = 1;
    private int ITEM_TYPE2 = 2;
    private int ITEM_TYPE3 = 3;
    private int ITEM_EXPAND = 4;

    private final LinkedList<PostInfo> PostData;
    private final LinkedList<PostInfo> mPost;
    private final Context context;
    private final LayoutInflater mInflater;

    class ExpandViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView ExpandItemView;
        final PostAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public ExpandViewHolder(View itemView, PostAdapter adapter) {
            super(itemView);
            ExpandItemView = itemView.findViewById(R.id.textViewExpand);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.

//            mNews.set(mPosition, "Clicked! " + element);
            // Notify the adapter, that the data has changed so it can
            int tmpcnt = mPost.size();

            for(int i = tmpcnt; i < Math.min(PostData.size(), tmpcnt + 10);i++) {
                mPost.addLast(PostData.get(i));
            }

            // update the RecyclerView to display the data.
            mAdapter.notifyDataSetChanged();
        }
    }

    class PostTextViewHolder extends RecyclerView.ViewHolder {
        public final TextView UsernameItemView, TiTleItemView, TextItemView,
                LocationItemView, TimeItemView, LikeItemView;
        public final RecyclerView CommentRecyclerView;
        public final ImageView AvatarImageView;
        public final Button SubscribeButton, BlockButton;
        public final ImageButton LikeButton, CommmentButton, ShareButton;
        final PostAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public PostTextViewHolder(View itemView, PostAdapter adapter) {
            super(itemView);
            UsernameItemView = itemView.findViewById(R.id.textViewUsernamePost);
            TiTleItemView = itemView.findViewById(R.id.textViewTitlePost);
            TextItemView = itemView.findViewById(R.id.textViewTextPost);
            LocationItemView = itemView.findViewById(R.id.textViewPlacePost);
            TimeItemView = itemView.findViewById(R.id.textViewTimePost);
            LikeItemView = itemView.findViewById(R.id.textViewLikePost);

            AvatarImageView = itemView.findViewById(R.id.imageViewAvatarPost);
            SubscribeButton = itemView.findViewById(R.id.buttonSubscribePost);
            BlockButton = itemView.findViewById(R.id.buttonBlockPost);

            LikeButton = itemView.findViewById(R.id.imageButtonLike);
            CommmentButton = itemView.findViewById(R.id.imageButtonComment);
            ShareButton = itemView.findViewById(R.id.imageButtonShare);

            CommentRecyclerView = itemView.findViewById(R.id.recyclerViewComment);

            this.mAdapter = adapter;
        }
    }


    public class MyCallBack implements HttpRequestManager.ReqCallBack {
        public int type;
        public MyCallBack(int type){
            this.type = type;
        }
        public void setType(int type){
            this.type = type;
        }
        @Override
        public void onReqSuccess(Object result) {

            Log.i("PostAdapter---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("PostAdapter---",errorMsg);
        }
    }

    public PostAdapter(Context context, LinkedList<PostInfo> PostData) {
        mInflater = LayoutInflater.from(context);
        this.PostData = PostData;
        this.context = context;
        this.mPost = new LinkedList<PostInfo>();
        for(int i = 0; i < Math.min(10, PostData.size());i++) {
            this.mPost.addLast(PostData.get(i));
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     *
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        if (viewType == ITEM_TYPE0) {
            // Inflate an item view.
            View mItemView = mInflater.inflate(
                    R.layout.item_post_text, parent, false);
            return new PostTextViewHolder(mItemView, this);
        }
        else {
            View mItemView = mInflater.inflate(
                    R.layout.expand_relation, parent, false);
            return new ExpandViewHolder(mItemView, this);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent
     *                 the contents of the item at the given position in the
     *                 data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        if (holder instanceof PostTextViewHolder) {

        }
        else if (holder instanceof ExpandViewHolder) {
            ExpandViewHolder tmpExpandViewHolder = (ExpandViewHolder) holder;
            String tmpText = "";
            if (PostData.size() == mPost.size()) {
                tmpText = "没有更多了";
            }
            else {
                tmpText = "点击查看更多";
            }
            tmpExpandViewHolder.ExpandItemView.setText(tmpText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mPost.size()) {
            return mPost.get(position).Type;
        }
        else if (position == mPost.size()) {
            return ITEM_EXPAND;
        }
        else return super.getItemViewType(position);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mPost.size() + 1;
    }
}
