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
import com.example.frontend.info.RelationInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

public class RelationAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int ITEM_RELATION = 1;
    private int ITEM_EXPAND = 2;

    private final LinkedList<RelationInfo> relationData;
    private final LinkedList<RelationInfo> mRelation;
    private final Context context;
    private final LayoutInflater mInflater;

    int tot=0;

    class ExpandViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView ExpandItemView;
        final RelationAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public ExpandViewHolder(View itemView, RelationAdapter adapter) {
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
            int tmpcnt = mRelation.size();

            for(int i = tmpcnt; i < Math.min(relationData.size(), tmpcnt + 10);i++) {
                mRelation.addLast(relationData.get(i));
            }

            // update the RecyclerView to display the data.
            mAdapter.notifyDataSetChanged();
        }
    }

    class RelationViewHolder extends RecyclerView.ViewHolder {
        public final TextView UsernameItemView;
        public final TextView IntroItemView;
        public final ImageView AvatarImageView;
        public final Button SubscribeButton, BlockButton;
        final RelationAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public RelationViewHolder(View itemView, RelationAdapter adapter) {
            super(itemView);
            UsernameItemView = itemView.findViewById(R.id.textViewUsernameRelation);
            IntroItemView = itemView.findViewById(R.id.textViewIntroRelation);
            AvatarImageView = itemView.findViewById(R.id.imageViewAvatarRelation);
            SubscribeButton = itemView.findViewById(R.id.buttonSubscribe);
            BlockButton = itemView.findViewById(R.id.buttonBlock);
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
                for(int i = 0; i < mRelation.size(); i++) {
                    if (mRelation.get(i).userid.equals(subscriberid)) {
                        mRelation.get(i).isSubscribe = 0;
                        relationData.get(i).isSubscribe = 0;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==3) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer subscriberid = res.getInteger("subscriberid");
                for(int i = 0; i < mRelation.size(); i++) {
                    if (mRelation.get(i).userid.equals(subscriberid)) {
                        mRelation.get(i).isSubscribe = 1;
                        relationData.get(i).isSubscribe = 1;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==4) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer blockerid = res.getInteger("blockerid");
                for(int i = 0; i < mRelation.size(); i++) {
                    if (mRelation.get(i).userid.equals(blockerid)) {
                        mRelation.get(i).isBlock = 0;
                        relationData.get(i).isBlock = 0;
                    }
                }
                notifyDataSetChanged();
            }
            else if (type==5) {
                JSONObject res = JSON.parseObject(result.toString());
                Integer blockerid = res.getInteger("blockerid");
                for(int i = 0; i < mRelation.size(); i++) {
                    if (mRelation.get(i).userid.equals(blockerid)) {
                        mRelation.get(i).isBlock = 1;
                        relationData.get(i).isBlock = 1;
                    }
                }
                notifyDataSetChanged();
            }
            Log.i("RelationAdapter---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("RelationAdapter---",errorMsg);
        }
    }

    public RelationAdapter(Context context, LinkedList<RelationInfo> relationData) {
        mInflater = LayoutInflater.from(context);
        this.relationData = relationData;
        this.context = context;
        this.mRelation = new LinkedList<RelationInfo>();
        for(int i = 0; i < Math.min(10, relationData.size());i++) {
            this.mRelation.addLast(relationData.get(i));
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
        if (viewType == ITEM_RELATION) {
            // Inflate an item view.
            View mItemView = mInflater.inflate(
                    R.layout.item_relation, parent, false);
            return new RelationViewHolder(mItemView, this);
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
        if (holder instanceof RelationViewHolder) {
            RelationViewHolder tmpRelationViewHolder = (RelationViewHolder) holder;
            RelationInfo tmpInfo = mRelation.get(position);
            tmpRelationViewHolder.UsernameItemView.setText(tmpInfo.username);
            tmpRelationViewHolder.IntroItemView.setText(tmpInfo.intro);

            tmpRelationViewHolder.UsernameItemView.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AccountActivity.class);
                    intent.putExtra("userid", tmpInfo.userid);
                    intent.putExtra("username", tmpInfo.username);
                    intent.putExtra("avatarid", tmpInfo.avatarid);
                    intent.putExtra("intro", tmpInfo.intro);
                    context.startActivity(intent);
                }
            });

            if (tmpInfo.isSubscribe == 1) {
                tmpRelationViewHolder.SubscribeButton.setText("取消关注");
                tmpRelationViewHolder.SubscribeButton.setOnClickListener(new Button.OnClickListener() {
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
                tmpRelationViewHolder.SubscribeButton.setText("关注");
                tmpRelationViewHolder.SubscribeButton.setOnClickListener(new Button.OnClickListener() {
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
            if (tmpInfo.isBlock == 1) {
                tmpRelationViewHolder.BlockButton.setText("取消屏蔽");
                tmpRelationViewHolder.BlockButton.setOnClickListener(new Button.OnClickListener() {
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
                tmpRelationViewHolder.BlockButton.setText("屏蔽");
                tmpRelationViewHolder.BlockButton.setOnClickListener(new Button.OnClickListener() {
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

            if (tmpInfo.avatarid == -1) {
                tmpRelationViewHolder.AvatarImageView.setImageResource(R.drawable.ic_avatar);
            }
            else if (tmpInfo.avatarFilename.equals("")) {
                tmpRelationViewHolder.AvatarImageView.setImageResource(R.drawable.ic_avatar);
            }
            else {
                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + tmpInfo.avatarFilename;
                if (FileManager.getInstance().getUserFileExists(context, "avatar" + "/" + tmpInfo.avatarFilename)) {
                    // set avatar
                    tmpRelationViewHolder.AvatarImageView.setImageDrawable(Drawable.createFromPath(fileAbsPath));
                }
                else {
                    String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                            + Integer.toString(tmpInfo.avatarid);
                    String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "avatar");
                    MyCallBack callback = new MyCallBack(1);
                    HttpRequestManager.getInstance(context).downLoadFile(url, tmpInfo.avatarFilename, destDir, callback);
                    Log.i("Download to", fileAbsPath);
                    tmpRelationViewHolder.AvatarImageView.setImageResource(R.drawable.ic_avatar);
                }
            }
            tmpRelationViewHolder.AvatarImageView.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AccountActivity.class);
                    intent.putExtra("userid", tmpInfo.userid);
                    intent.putExtra("username", tmpInfo.username);
                    intent.putExtra("avatarid", tmpInfo.avatarid);
                    intent.putExtra("intro", tmpInfo.intro);
                    context.startActivity(intent);
                }
            });
        }
        else if (holder instanceof ExpandViewHolder) {
            ExpandViewHolder tmpExpandViewHolder = (ExpandViewHolder) holder;
            String tmpText = "";
            if (relationData.size() == mRelation.size()) {
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
        if (position < mRelation.size()) {
            return ITEM_RELATION;
        }
        else if (position == mRelation.size()) {
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
        return mRelation.size() + 1;
    }
}
