package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;

import butterknife.ButterKnife;

public class SettingPasswordActivity extends AppCompatActivity {

    EditText editTextOld, editTextNew;

    Button buttonSubmit;

    String EditOld, EditNew;

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
                Toast.makeText(getApplicationContext(),
                        "更改密码成功",
                        Toast.LENGTH_SHORT).show();
                clearAndLogout();
            }
            Log.i("SettingsFrag---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(context,
                        "更改密码失败",
                        Toast.LENGTH_SHORT).show();
            }
            Log.e("SettingsFrag---",errorMsg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        setTitle("设置密码");
        ButterKnife.bind(this);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();

        editTextOld = findViewById(R.id.editTextOldPassword);
        editTextNew = findViewById(R.id.editTextNewPassword);
        buttonSubmit = findViewById(R.id.buttonSubmit2);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "submit click");

                EditOld = editTextOld.getText().toString();
                EditNew = editTextNew.getText().toString();
                if (EditOld.equals("")) {
                    Toast.makeText(context,
                            "请输入旧密码",
                            Toast.LENGTH_SHORT).show();
                }
                else if (EditNew.equals("")) {
                    Toast.makeText(context,
                            "请输入新密码",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    HttpRequestManager http = HttpRequestManager.getInstance(context);
                    MyCallBack callBack = new MyCallBack(1);
                    HashMap<String, String> data = new HashMap<>();
                    data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                    data.put("oldpassword", EditOld);
                    data.put("newpassword", EditNew);
                    http.requestAsyn("api/user/passwd",1, data, callBack);
                }
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

        Intent intent = new Intent(SettingPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        SettingPasswordActivity.this.finish();
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