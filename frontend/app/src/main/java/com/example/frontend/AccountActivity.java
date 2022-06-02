package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.adapter.PostAdapter;
import com.example.frontend.info.PostInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

import butterknife.ButterKnife;

public class AccountActivity extends AppCompatActivity {

    TextView textViewUsername, textViewIntro;
    ImageView imageViewAvatar;

    Context context;

    String username, intro, avatarFilename;
    Integer userid, avatarID;

    PostAdapter postAdapter;

    RecyclerView recyclerView;

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
                JSONObject res = JSON.parseObject(result.toString());
                String filename = res.getString("filename");
                avatarFilename = filename;

                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + filename;

                if (FileManager.getInstance().getUserFileExists(context, "avatar" + "/" + filename)) {
                    // set avatar
                    imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
                }
                else {
                    String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                            + Integer.toString(avatarID);
                    String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "avatar");
                    MyCallBack callback = new MyCallBack(2);
                    HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                    Log.i("Download to", fileAbsPath);
                }
            }
            else if (type==2) {
                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + avatarFilename;
                imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
            }
            else if (type==17) {
                JSONArray nowList = JSON.parseObject(result.toString()).getJSONArray("list");
                LinkedList<PostInfo> postInfoLinkedList = new LinkedList<>();
                for(int i=0;i<nowList.size();i++) {
                    JSONObject tmpInfo = nowList.getJSONObject(i);
                    postInfoLinkedList.addLast(new PostInfo(tmpInfo.getInteger("postid"),
                            tmpInfo.getInteger("userid"),
                            tmpInfo.getString("username"),
                            tmpInfo.getInteger("avatarid"),
                            tmpInfo.getString("avatarfilename"),
                            tmpInfo.getString("intro"),
                            tmpInfo.getInteger("fileid"),
                            tmpInfo.getString("filename"),
                            tmpInfo.getString("title"),
                            tmpInfo.getString("text"),
                            tmpInfo.getInteger("type"),
                            tmpInfo.getString("time"),
                            tmpInfo.getString("location"),
                            tmpInfo.getInteger("subscribe"),
                            tmpInfo.getInteger("block")));
                }
                postAdapter = new PostAdapter(AccountActivity.this, postInfoLinkedList);
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            Log.i("AccountActi---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(context,
                        "载入头像失败",
                        Toast.LENGTH_SHORT).show();
                imageViewAvatar.setImageResource(R.drawable.ic_avatar);
            }
            else if (type==2) {
                Toast.makeText(context,
                        "载入头像失败",
                        Toast.LENGTH_SHORT).show();
                imageViewAvatar.setImageResource(R.drawable.ic_avatar);
            }
            Log.e("AccountActi---",errorMsg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setTitle("用户信息");
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userid = intent.getIntExtra("userid",-1);
        username = intent.getStringExtra("username");
        intro = intent.getStringExtra("intro");
        avatarID = intent.getIntExtra("avatarid", -1);
        avatarFilename = "";

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        textViewUsername = (TextView) findViewById(R.id.textViewUsernameAccount);
        textViewIntro = (TextView) findViewById(R.id.textViewIntroAccount);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatarAccount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAccountActivity);

        context = getApplicationContext();

        // set avatar username intro
        setInfo();
    }

    public void setInfo() {
        textViewUsername.setText(username);
        textViewIntro.setText(intro);
        if (avatarID != -1) {
            Log.i("Download", "Avatar");

            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callBack = new MyCallBack(1);
            HashMap<String, String> data = new HashMap<>();
            data.put("fileid", Integer.toString(avatarID));
            http.requestAsyn("api/file/filename",0, data, callBack);
        }
        else {
            imageViewAvatar.setImageResource(R.drawable.ic_avatar);
        }

        HttpRequestManager http = HttpRequestManager.getInstance(context);
        MyCallBack callBack = new MyCallBack(17);
        HashMap<String, String> data = new HashMap<>();
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        data.put("posterid", Integer.toString(userid));
        http.requestAsyn("api/discover/allpost",0, data, callBack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}