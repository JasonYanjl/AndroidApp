package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import java.io.File;
import java.util.HashMap;
import java.lang.Exception;

import butterknife.ButterKnife;
import okhttp3.Call;

public class SettingsActivity extends AppCompatActivity {

    TextView textViewUsername, textViewIntro;

    ImageView imageViewAvatar;

    Context context;

    LinearLayout linearLayoutAvatar, linearLayoutUsername, linearLayoutIntro, linearLayoutPassword;

    Button buttonLogout;

    Integer newAvatarID;

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

                if (FileManager.getInstance().getUserFileExists(context, "avatar" + "/" + filename)) {
                    // set avatar
                    imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
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
            else if (type==3) {
                JSONObject res = JSON.parseObject(result.toString());
                newAvatarID = res.getInteger("fileid");

                MyCallBack callback = new MyCallBack(4);
                HashMap<String, String> data = new HashMap<>();
                data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                data.put("key", "avatarid");
                data.put("desc", Integer.toString(newAvatarID));
                HttpRequestManager.getInstance(context).requestAsyn("api/user/modify",1, data, callback);
            }
            else if (type==4) {
                UserInfo.getInstance().setAvatarid(newAvatarID);
                setInfo();
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
            else if (type==3) {
                Toast.makeText(context,
                        "更新头像失败",
                        Toast.LENGTH_SHORT).show();
            }
            else if (type==4) {
                Toast.makeText(context,
                        "更新头像失败",
                        Toast.LENGTH_SHORT).show();
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
        linearLayoutAvatar.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Avatar Click");

                checkPermission();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0x000101);
            }
        });

        linearLayoutUsername.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Username Click");

                Intent intent = new Intent(SettingsActivity.this, SettingUsernameActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutIntro.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Intro Click");

                Intent intent = new Intent(SettingsActivity.this, SettingIntroActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutPassword.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("click", "Password Click");

                Intent intent = new Intent(SettingsActivity.this, SettingPasswordActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "logout click");

                clearAndLogout();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", Integer.toString(requestCode));
        if(requestCode == 0x000101){
            if (resultCode == Activity.RESULT_OK) {
                //判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4及以上系统使用这个方法处理图片
                    //TODO: send http requests
                    String imagePath = "";
                    Uri uri = data.getData();
                    Log.i("Author",uri.getAuthority());
                    if (DocumentsContract.isDocumentUri(context, uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            Log.i("type","com.android.providers.media.documents");
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        }
                        else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            Log.i("type","com.android.providers.downloads.documents");
                            Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        }
                        else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            Log.i("type","file");
                            imagePath = uri.getPath();
                        }
                    }
                    else if ("content".equalsIgnoreCase(uri.getScheme())) {
                        Log.i("type","content");
                        imagePath = getImagePath(uri, null);
                    }
                    File nowAvatar = new File(imagePath);
                    HttpRequestManager http = HttpRequestManager.getInstance(getApplicationContext());
                    MyCallBack callback = new MyCallBack(3);
                    HashMap<String, Object> photoData = new HashMap<>();
                    photoData.put("file", nowAvatar);
                    photoData.put("userid", UserInfo.getInstance().getUserid());
                    photoData.put("type", Integer.toString(1));
                    http.upLoadFile("api/file/upload", photoData, callback);
                }
            }
        }
        else {

        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                // wrong path
                if (index < 0) return null;
                path = cursor.getString(index);
            }

            cursor.close();
        }
        return path;
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

    private void checkPermission() {
        //first we need check this Drive has? CAMERA Permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            return;
        }

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