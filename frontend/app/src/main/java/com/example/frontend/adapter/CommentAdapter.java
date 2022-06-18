package com.example.frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.frontend.info.CommentInfo;
import com.example.frontend.info.RelationInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

public class CommentAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int ITEM_COMMENT = 1;

    private final LinkedList<CommentInfo> mComment;
    private final LayoutInflater mInflater;

    Context context;


    class CommentViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewCommentUsername, textViewCommentText, textViewCommentDelete;
        final CommentAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public CommentViewHolder(View itemView, CommentAdapter adapter) {
            super(itemView);
            textViewCommentUsername = itemView.findViewById(R.id.textViewCommentUsername);
            textViewCommentText = itemView.findViewById(R.id.textViewCommentText);
            textViewCommentDelete = itemView.findViewById(R.id.textViewCommentDelete);
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
            if (type==1) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpCommentid = res.getInteger("commentid");
                for(int i = 0; i < mComment.size(); i++) {
                    if (mComment.get(i).commentid.equals(tmpCommentid)) {
                        mComment.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
            Log.i("CommentAdapter---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("CommentAdapter---",errorMsg);
        }
    }

    public CommentAdapter(Context context, LinkedList<CommentInfo> CommentData) {
        mInflater = LayoutInflater.from(context);
        this.mComment = CommentData;
        this.context = context;
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

            // Inflate an item view.
            View mItemView = mInflater.inflate(
                    R.layout.item_comment, parent, false);
            return new CommentViewHolder(mItemView, this);

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
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder tmpCommentViewHolder = (CommentViewHolder) holder;
            CommentInfo tmpInfo = mComment.get(position);

            tmpCommentViewHolder.textViewCommentUsername.setText(tmpInfo.username);
            tmpCommentViewHolder.textViewCommentUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AccountActivity.class);
                    intent.putExtra("userid", tmpInfo.userid);
                    intent.putExtra("username", tmpInfo.username);
                    intent.putExtra("avatarid", tmpInfo.avatarid);
                    intent.putExtra("intro", tmpInfo.Intro);
                    context.startActivity(intent);
                }
            });

            tmpCommentViewHolder.textViewCommentText.setText(tmpInfo.Text);

            if (tmpInfo.userid.equals(UserInfo.getInstance().getUserid())) {
                tmpCommentViewHolder.textViewCommentDelete.setVisibility(View.VISIBLE);
                tmpCommentViewHolder.textViewCommentDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(1);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(tmpInfo.userid));
                        data.put("commentid", Integer.toString(tmpInfo.commentid));
                        http.requestAsyn("api/discover/cancelcomment",1, data, callBack);
                    }
                });
            }
            else {
                tmpCommentViewHolder.textViewCommentDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_COMMENT;
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mComment.size();
    }
}