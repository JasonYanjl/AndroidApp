package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class SettingIntroActivity extends AppCompatActivity {

    EditText editTextIntro;

    Button buttonSubmit;

    String EditIntro;

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
                UserInfo.getInstance().setIntro(EditIntro);
                SettingIntroActivity.this.finish();
            }
            Log.i("SettingsFrag---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(context,
                        "更改简介失败",
                        Toast.LENGTH_SHORT).show();
            }
            Log.e("SettingsFrag---",errorMsg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_intro);
        setTitle("设置简介");
        ButterKnife.bind(this);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();

        editTextIntro = findViewById(R.id.editTextIntro);
        buttonSubmit = findViewById(R.id.buttonSubmit1);

        editTextIntro.setText(UserInfo.getInstance().getIntro());

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "submit click");

                EditIntro = editTextIntro.getText().toString();
                if (EditIntro.equals(UserInfo.getInstance().getIntro())) {
                    Toast.makeText(context,
                            "请修改简介",
                            Toast.LENGTH_SHORT).show();
                }
                else if (EditIntro.equals("")) {
                    Toast.makeText(context,
                            "请输入简介",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    HttpRequestManager http = HttpRequestManager.getInstance(context);
                    MyCallBack callBack = new MyCallBack(1);
                    HashMap<String, String> data = new HashMap<>();
                    data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
                    data.put("key", "intro");
                    data.put("desc", EditIntro);
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