package com.example.frontend.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.AccountActivity;
import com.example.frontend.PhotoActivity;
import com.example.frontend.R;
import com.example.frontend.VideoActivity;
import com.example.frontend.info.CommentInfo;
import com.example.frontend.info.LikeInfo;
import com.example.frontend.info.PostInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.recorder.Recorder;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

public class PostAdapter_search extends
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
        final PostAdapter_search mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public ExpandViewHolder(View itemView, PostAdapter_search adapter) {
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
        public final Button SubscribeButton, BlockButton, ButtonVoice;
        public final ImageButton LikeButton, CommmentButton, ShareButton;
        public final ImageView ImageViewImage;
        public CommentAdapter commentAdapter;
        final PostAdapter_search mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public PostTextViewHolder(View itemView, PostAdapter_search adapter) {
            super(itemView);
            UsernameItemView = itemView.findViewById(R.id.textViewUsernamePostSearch);
            TiTleItemView = itemView.findViewById(R.id.textViewTitlePostSearch);
            TextItemView = itemView.findViewById(R.id.textViewTextPostSearch);
            LocationItemView = itemView.findViewById(R.id.textViewPlacePostSearch);
            TimeItemView = itemView.findViewById(R.id.textViewTimePostSearch);
            LikeItemView = itemView.findViewById(R.id.textViewLikePostSearch);

            AvatarImageView = itemView.findViewById(R.id.imageViewAvatarPostSearch);
            SubscribeButton = itemView.findViewById(R.id.buttonSubscribePostSearch);
            BlockButton = itemView.findViewById(R.id.buttonBlockPostSearch);

            LikeButton = itemView.findViewById(R.id.imageButtonLikeSearch);
            CommmentButton = itemView.findViewById(R.id.imageButtonCommentSearch);
            ShareButton = itemView.findViewById(R.id.imageButtonShareSearch);

            ButtonVoice = itemView.findViewById(R.id.buttonVoiceSearch);

            CommentRecyclerView = itemView.findViewById(R.id.recyclerViewCommentSearch);

            ImageViewImage = itemView.findViewById(R.id.imageViewImageSearch);

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
            if (type == 1) {
                notifyDataSetChanged();
            }
            else if (type==2) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer subscriberid = res.getInteger("subscriberid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).userid.equals(subscriberid)) {
                        mPost.get(i).isSubscribe = 0;
                        PostData.get(i).isSubscribe = 0;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==3) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer subscriberid = res.getInteger("subscriberid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).userid.equals(subscriberid)) {
                        mPost.get(i).isSubscribe = 1;
                        PostData.get(i).isSubscribe = 1;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==4) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer blockerid = res.getInteger("blockerid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).userid.equals(blockerid)) {
                        mPost.get(i).isBlock = 0;
                        PostData.get(i).isBlock = 0;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==5) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer blockerid = res.getInteger("blockerid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).userid.equals(blockerid)) {
                        mPost.get(i).isBlock = 1;
                        PostData.get(i).isBlock = 1;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==6) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpPostID = res.getInteger("postid");
                JSONArray tmpArray = res.getJSONArray("list");
                LinkedList<LikeInfo> tmpLike = new LinkedList<LikeInfo>();
                for(int i=0;i<tmpArray.size();i++) {
                    tmpLike.addLast(new LikeInfo(tmpArray.getJSONObject(i).getInteger("userid"),
                            tmpArray.getJSONObject(i).getString("username")));
                }
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).postid.equals(tmpPostID)) {
                        mPost.get(i).catchLike = true;
                        mPost.get(i).Like = tmpLike;
                        PostData.get(i).catchLike = true;
                        PostData.get(i).Like = tmpLike;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==7) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpPostID = res.getInteger("postid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).postid.equals(tmpPostID)) {
                        mPost.get(i).catchLike = false;
                        PostData.get(i).catchLike = false;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==8) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpPostID = res.getInteger("postid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).postid.equals(tmpPostID)) {
                        mPost.get(i).catchLike = false;
                        PostData.get(i).catchLike = false;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==9) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpPostID = res.getInteger("postid");
                JSONArray tmpArray = res.getJSONArray("list");
                LinkedList<CommentInfo> tmpComment = new LinkedList<CommentInfo>();
                for(int i=0;i<tmpArray.size();i++) {
                    JSONObject tmpObject = tmpArray.getJSONObject(i);
                    tmpComment.addLast(new CommentInfo(tmpObject.getInteger("userid"),
                            tmpObject.getString("username"),
                            tmpObject.getInteger("avatarid"),
                            tmpObject.getString("intro"),
                            tmpObject.getInteger("commentid"),
                            tmpObject.getString("text"),
                            tmpObject.getString("time")));
                }
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).postid.equals(tmpPostID)) {
                        mPost.get(i).catchComment = true;
                        mPost.get(i).Comment = tmpComment;
                        PostData.get(i).catchComment = true;
                        PostData.get(i).Comment = tmpComment;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==10) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer tmpPostID = res.getInteger("postid");
                for(int i=0;i<mPost.size();i++) {
                    if (mPost.get(i).postid.equals(tmpPostID)) {
                        mPost.get(i).catchComment = false;
                        PostData.get(i).catchComment = false;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==11) {
                notifyDataSetChanged();
            }
            Log.i("PostAdapter---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("PostAdapter---",errorMsg);
        }
    }

    public PostAdapter_search(Context context, LinkedList<PostInfo> PostData) {
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
                    R.layout.item_post_text_search, parent, false);
            return new PostTextViewHolder(mItemView, this);
        }
        else if (viewType == ITEM_TYPE1) {
            // Inflate an item view.
            View mItemView = mInflater.inflate(
                    R.layout.item_post_text_search, parent, false);
            return new PostTextViewHolder(mItemView, this);
        }
        else if (viewType == ITEM_TYPE2) {
            View mItemView = mInflater.inflate(
                    R.layout.item_post_text_search, parent, false);
            return new PostTextViewHolder(mItemView, this);
        }
        else if (viewType == ITEM_TYPE3) {
            View mItemView = mInflater.inflate(
                    R.layout.item_post_text_search, parent, false);
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
            PostTextViewHolder tmpPostTextViewHolder = (PostTextViewHolder) holder;
            PostInfo tmpInfo = mPost.get(position);

            //avatar
            if (tmpInfo.avatarid == -1 || tmpInfo.avatarFilename.equals("")) {
                tmpPostTextViewHolder.AvatarImageView.setImageResource(R.drawable.ic_avatar);
            }
            else {
                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + tmpInfo.avatarFilename;
                if (FileManager.getInstance().getUserFileExists(context, "avatar" + "/" + tmpInfo.avatarFilename)) {
                    // set avatar
                    tmpPostTextViewHolder.AvatarImageView.setImageDrawable(Drawable.createFromPath(fileAbsPath));
                }
                else {
                    String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                            + Integer.toString(tmpInfo.avatarid);
                    String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "avatar");
                    MyCallBack callback = new MyCallBack(1);
                    HttpRequestManager.getInstance(context).downLoadFile(url, tmpInfo.avatarFilename, destDir, callback);
                    Log.i("Download to", fileAbsPath);
                    tmpPostTextViewHolder.AvatarImageView.setImageResource(R.drawable.ic_avatar);
                }
            }
            //username
            tmpPostTextViewHolder.UsernameItemView.setText(tmpInfo.username);
            tmpPostTextViewHolder.UsernameItemView.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AccountActivity.class);
                    intent.putExtra("userid", tmpInfo.userid);
                    intent.putExtra("username", tmpInfo.username);
                    intent.putExtra("avatarid", tmpInfo.avatarid);
                    intent.putExtra("intro", tmpInfo.intro);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            //title
            tmpPostTextViewHolder.TiTleItemView.setText(tmpInfo.Title);
            //text
            tmpPostTextViewHolder.TextItemView.setText(tmpInfo.Text);
            //time
            tmpPostTextViewHolder.TimeItemView.setText("发布于:"+tmpInfo.Time);
            //subscribe
            if (tmpInfo.isSubscribe == 1) {
                tmpPostTextViewHolder.SubscribeButton.setText("取消关注");
                tmpPostTextViewHolder.SubscribeButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(2);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("subscriberid", Integer.toString(tmpInfo.userid));
                        http.requestAsyn("api/user/unsubscribe",1, data, callBack);
                    }
                });
            }
            else {
                tmpPostTextViewHolder.SubscribeButton.setText("关注");
                tmpPostTextViewHolder.SubscribeButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(3);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("subscriberid", Integer.toString(tmpInfo.userid));
                        http.requestAsyn("api/user/subscribe",1, data, callBack);
                    }
                });
            }
            //block
            if (tmpInfo.isBlock == 1) {
                tmpPostTextViewHolder.BlockButton.setText("取消屏蔽");
                tmpPostTextViewHolder.BlockButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(4);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("blockerid", Integer.toString(tmpInfo.userid));
                        http.requestAsyn("api/user/unblock",1, data, callBack);
                    }
                });
            }
            else {
                tmpPostTextViewHolder.BlockButton.setText("屏蔽");
                tmpPostTextViewHolder.BlockButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(5);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("blockerid", Integer.toString(tmpInfo.userid));
                        http.requestAsyn("api/user/block",1, data, callBack);
                    }
                });
            }
            //location
            if (tmpInfo.Location.equals("")) {
                tmpPostTextViewHolder.LocationItemView.setVisibility(View.GONE);
            }
            else {
                tmpPostTextViewHolder.LocationItemView.setVisibility(View.VISIBLE);
                tmpPostTextViewHolder.LocationItemView.setText(tmpInfo.Location);
                tmpPostTextViewHolder.LocationItemView.setClickable(true);
                tmpPostTextViewHolder.LocationItemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // Parse the location and create the intent.
                        Uri addressUri = Uri.parse("geo:0,0?q=" + tmpInfo.Location);
                        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // Start the activity.
                        context.startActivity(intent);
                    }
                });
            }
            //Like
            if (tmpInfo.catchLike.equals(false)) {
                HttpRequestManager http = HttpRequestManager.getInstance(context);
                MyCallBack callBack = new MyCallBack(6);
                HashMap<String, String> data = new HashMap<>();
                data.put("postid", Integer.toString(tmpInfo.postid));
                http.requestAsyn("api/discover/collectlike",0, data, callBack);
            }
            if (tmpInfo.Like.size() == 0) {
                tmpPostTextViewHolder.LikeItemView.setVisibility(View.GONE);
            }
            else {
                tmpPostTextViewHolder.LikeItemView.setVisibility(View.VISIBLE);
                tmpPostTextViewHolder.LikeItemView.setText(tmpInfo.LikeList2String());
            }
            //LikeButton
            if (checkUserInLike(UserInfo.getInstance().getUserid(), tmpInfo.Like)) {
                tmpPostTextViewHolder.LikeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_thumb_up_24));
                tmpPostTextViewHolder.LikeButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(7);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("postid", Integer.toString(tmpInfo.postid));
                        http.requestAsyn("api/discover/dislike",1, data, callBack);
                    }
                });
            }
            else {
                tmpPostTextViewHolder.LikeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_outline_thumb_up_24));
                tmpPostTextViewHolder.LikeButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        HttpRequestManager http = HttpRequestManager.getInstance(context);
                        MyCallBack callBack = new MyCallBack(8);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                        data.put("postid", Integer.toString(tmpInfo.postid));
                        http.requestAsyn("api/discover/like",1, data, callBack);
                    }
                });
            }
            //ShareButton
            tmpPostTextViewHolder.ShareButton.setOnClickListener(v->{
                    String mimeType = "text/plain";
                    String txt = "标题："+tmpInfo.Title + "\n"
                            + "正文：" + tmpInfo.Text + "\n"
                            + "发表时间：" + tmpInfo.Time + "\n";

                    ShareCompat.IntentBuilder
                            .from((Activity) context)
                            .setType(mimeType)
                            .setChooserTitle("Share this text with:")
                            .setText(txt)
                            .startChooser();
            });
            //CommmentButton

            tmpPostTextViewHolder.CommmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText inputServer = new EditText(context);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("评论").setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            HttpRequestManager http = HttpRequestManager.getInstance(context);
                            MyCallBack callBack = new MyCallBack(10);
                            HashMap<String, String> data = new HashMap<>();
                            data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                            data.put("postid", Integer.toString(tmpInfo.postid));
                            data.put("text", inputServer.getText().toString());
                            http.requestAsyn("api/discover/comment",1, data, callBack);
                        }
                    });
                    builder.show();
                }
            });

            //Comment
            if (tmpInfo.catchComment.equals(false)) {
                HttpRequestManager http = HttpRequestManager.getInstance(context);
                MyCallBack callBack = new MyCallBack(9);
                HashMap<String, String> data = new HashMap<>();
                data.put("postid", Integer.toString(tmpInfo.postid));
                http.requestAsyn("api/discover/collectcomment",0, data, callBack);
            }
            if (tmpInfo.Comment.size()==0) {
                tmpPostTextViewHolder.CommentRecyclerView.setVisibility(View.GONE);
            }
            else {
                tmpPostTextViewHolder.CommentRecyclerView.setVisibility(View.VISIBLE);
                tmpPostTextViewHolder.commentAdapter = new CommentAdapter(context, tmpInfo.Comment);
                tmpPostTextViewHolder.CommentRecyclerView.setAdapter(tmpPostTextViewHolder.commentAdapter);
                tmpPostTextViewHolder.CommentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            //image
            if (mPost.get(position).Type.equals(ITEM_TYPE0)) {
                tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE1)) {
                if (tmpInfo.fileid.equals(-1) || tmpInfo.filename.equals("")) {
                    tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);
                }
                else {
                    String filename = tmpInfo.filename;
                    String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "image")
                            + "/" + filename;
                    if (FileManager.getInstance().getUserFileExists(context, "image" + "/" + filename)) {
                        // set image
                        tmpPostTextViewHolder.ImageViewImage.setVisibility(View.VISIBLE);
                        tmpPostTextViewHolder.ImageViewImage.setImageDrawable(Drawable.createFromPath(fileAbsPath));
                        tmpPostTextViewHolder.ImageViewImage.setClickable(true);
                        tmpPostTextViewHolder.ImageViewImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("click", "photo click");

                                Intent intent = new Intent(context, PhotoActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("fileAbsPath", fileAbsPath);
                                context.startActivity(intent);
                            }
                        });
                    }
                    else {
                        tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);

                        String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                                + Integer.toString(tmpInfo.fileid);
                        String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "image");
                        MyCallBack callback = new MyCallBack(11);
                        HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                        Log.i("Download to", fileAbsPath);
                    }
                }
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE2)) {
                tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE3)) {
                if (tmpInfo.fileid.equals(-1) || tmpInfo.filename.equals("")) {
                    tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);
                }
                else {
                    String filename = tmpInfo.filename;
                    String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "video")
                            + "/" + filename;
                    if (FileManager.getInstance().getUserFileExists(context, "video" + "/" + filename)) {
                        // set image
                        try {
                            Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                                    fileAbsPath, MediaStore.Video.Thumbnails.MINI_KIND);
                            if (videoThumbnail == null) {
                                Log.e("NULL","videoThumbnail is null");
                            }
                            else {
                                tmpPostTextViewHolder.ImageViewImage.setImageBitmap(videoThumbnail);
                                tmpPostTextViewHolder.ImageViewImage.setVisibility(View.VISIBLE);
                                tmpPostTextViewHolder.ImageViewImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.i("click", "video click");

                                        Intent intent = new Intent(context, VideoActivity.class);
                                        intent.putExtra("fileAbsPath", fileAbsPath);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }
                        catch (Exception e) {
                            Log.e("Error", "setGone");
                            tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }
                    }
                    else {
                        tmpPostTextViewHolder.ImageViewImage.setVisibility(View.GONE);

                        String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                                + Integer.toString(tmpInfo.fileid);
                        String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "video");
                        MyCallBack callback = new MyCallBack(11);
                        HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                        Log.i("Download to", fileAbsPath);
                    }
                }
            }
            //voice
            if (mPost.get(position).Type.equals(ITEM_TYPE0)) {
                tmpPostTextViewHolder.ButtonVoice.setVisibility(View.GONE);
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE1)) {
                tmpPostTextViewHolder.ButtonVoice.setVisibility(View.GONE);
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE2)) {
                if (tmpInfo.fileid.equals(-1) || tmpInfo.filename.equals("")) {
                    tmpPostTextViewHolder.ButtonVoice.setVisibility(View.GONE);
                }
                else {
                    String filename = tmpInfo.filename;
                    String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "voice")
                            + "/" + filename;

                    if (FileManager.getInstance().getUserFileExists(context, "voice" + "/" + filename)) {
                        // set voice
                        try{
                            MediaPlayer mp = MediaPlayer.create(context, Uri.parse(fileAbsPath));
                            if(mp!=null) {
                                int duration = mp.getDuration();
                                Log.e("duration", Integer.toString(duration));
                                tmpPostTextViewHolder.ButtonVoice.getLayoutParams().width = Math.max(250, duration / 10);
                                tmpPostTextViewHolder.ButtonVoice.setText("语音" + Integer.toString(duration / 1000) + "秒");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        tmpPostTextViewHolder.ButtonVoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Recorder.getInstance(context).play(fileAbsPath);
                            }
                        });
                        tmpPostTextViewHolder.ButtonVoice.setVisibility(View.VISIBLE);
                    }
                    else {
                        tmpPostTextViewHolder.ButtonVoice.setVisibility(View.GONE);

                        String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                                + Integer.toString(tmpInfo.fileid);
                        String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "voice");
                        MyCallBack callback = new MyCallBack(11);
                        HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                        Log.i("Download to", fileAbsPath);
                    }
                }
            }
            else if (mPost.get(position).Type.equals(ITEM_TYPE3)) {
                tmpPostTextViewHolder.ButtonVoice.setVisibility(View.GONE);
            }
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

    public boolean checkUserInLike(Integer Userid, LinkedList<LikeInfo> tmpLike) {
        for(int i=0;i<tmpLike.size();i++) {
            if (tmpLike.get(i).userid.equals(Userid)) return true;
        }
        return false;
    }

}
