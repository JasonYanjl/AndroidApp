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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;

import butterknife.ButterKnife;

public class SettingUsernameActivity extends AppCompatActivity {

    EditText editTextUsername;

    Button buttonSubmit;

    String EditUsername;

    Context context;

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
                UserInfo.getInstance().setUsername(EditUsername);
                SettingUsernameActivity.this.finish();
            }
            Log.i("SettingsFrag---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(context,
                        "更改用户名失败，用户名可能被占用",
                        Toast.LENGTH_SHORT).show();
            }
            Log.e("SettingsFrag---",errorMsg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_username);
        setTitle("设置用户名");
        ButterKnife.bind(this);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();

        editTextUsername = findViewById(R.id.editTextUsername2);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        editTextUsername.setText(UserInfo.getInstance().getUsername());

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "submit click");

                EditUsername = editTextUsername.getText().toString();
                if (EditUsername.equals(UserInfo.getInstance().getUsername())) {
                    Toast.makeText(context,
                            "请修改用户名",
                            Toast.LENGTH_SHORT).show();
                }
                else if (EditUsername.equals("")) {
                    Toast.makeText(context,
                            "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    HttpRequestManager http = HttpRequestManager.getInstance(context);
                    MyCallBack callBack = new MyCallBack(1);
                    HashMap<String, String> data = new HashMap<>();
                    data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                    data.put("key", "username");
                    data.put("desc", EditUsername);
                    http.requestAsyn("api/user/modify",1, data, callBack);
                }
            }
        });
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