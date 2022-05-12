package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;

import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    TextView textViewUsername, textViewIntro;

    ImageView imageViewAvatar;

    Context context;

    LinearLayout linearLayoutAvatar, linearLayoutUsername, linearLayoutIntro, linearLayoutPassword;

    Button buttonLogout;

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
                UserInfo.getInstance().setAvatarFilename(filename);

                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + filename;

                if (FileManager.getInstance().getUserFileExists(context, fileAbsPath)) {
                    // set avatar
                }
                else {
                    String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                            + Integer.toString(UserInfo.getInstance().getAvatarid());
                    String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "avatar");
                    MyCallBack callback = new MyCallBack(2);
                    HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                    Log.i("Download to", fileAbsPath);
                }
            }
            else if (type==2) {
                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + UserInfo.getInstance().getAvatarFilename();
                imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
            }
            Log.i("SettingsFrag---",result.toString());
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
            Log.e("SettingsFrag---",errorMsg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("SettingActivity", "onResume");
        setInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i("SettingActivity", "Oncreate");
        setContentView(R.layout.activity_settings);
        setTitle("个人信息设置");
        ButterKnife.bind(this);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        textViewUsername = findViewById(R.id.textViewUsername1);
        textViewIntro = findViewById(R.id.textViewIntro1);
        imageViewAvatar = findViewById(R.id.imageViewAvatar1);
        linearLayoutAvatar = findViewById(R.id.linearLayoutAvatar);
        linearLayoutUsername = findViewById(R.id.linearLayoutUsername);
        linearLayoutIntro = findViewById(R.id.linearLayoutIntro);
        linearLayoutPassword = findViewById(R.id.linearLayoutPassword);
        buttonLogout = findViewById(R.id.buttonLogout);

        // set avatar username intro
        setInfo();

        //
        linearLayoutAvatar.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Avatar Click");
            }
        });

        linearLayoutUsername.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Username Click");

                Intent intent = new Intent(SettingsActivity.this, SettingUsernameActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutIntro.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Intro Click");

                Intent intent = new Intent(SettingsActivity.this, SettingIntroActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutPassword.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Password Click");

                Intent intent = new Intent(SettingsActivity.this, SettingPasswordActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "logout click");

                clearAndLogout();
            }
        });
    }

    public void clearAndLogout() {
        UserInfo.getInstance().clearArray();
        UserInfo.getInstance().setJwt("");
        UserInfo.getInstance().setUserid(-1);
        UserInfo.getInstance().setUsername("");
        UserInfo.getInstance().setAvatarid(-1);
        UserInfo.getInstance().setIntro("");

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        SettingsActivity.this.finish();
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

    public void setInfo() {
        textViewUsername.setText(UserInfo.getInstance().getUsername());
        textViewIntro.setText(UserInfo.getInstance().getIntro());
        if (UserInfo.getInstance().getAvatarid() != -1) {
            Log.i("Download", "Avatar");

            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callBack = new MyCallBack(1);
            HashMap<String, String> data = new HashMap<>();
            data.put("fileid", Integer.toString(UserInfo.getInstance().getAvatarid()));
            http.requestAsyn("api/file/filename",0, data, callBack);
        }
        else {
            imageViewAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }
}