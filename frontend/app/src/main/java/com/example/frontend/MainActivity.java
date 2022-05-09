package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText mailEdit;
    private EditText codeEdit;
    private Button login;
    private Button register;
    private Button getcode;

    private String username, password, mail, checkcode;


    public class MyCallBack implements HttpRequestManager.ReqCallBack<String> {
        public int type;
        public MyCallBack(int type){
            this.type = type;
        }
        public void setType(int type){
            this.type = type;
        }
        @Override
        public void onReqSuccess(String result) {
            if (type == 1) {
                JSONObject obj = JSON.parseObject(result);

                UserInfo.getInstance().setUserid(obj.getInteger("userid"));
                UserInfo.getInstance().setUsername(obj.getString("username"));
                UserInfo.getInstance().setJwt(obj.getString("jwt"));

                Intent intent = new Intent(MainActivity.this, FrontpageActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
            else if (type==2) {
                Toast.makeText(getApplicationContext(),
                        "已发送验证码",
                        Toast.LENGTH_SHORT).show();
            }
            else if (type==3) {
                Toast.makeText(getApplicationContext(),
                        "验证成功",
                        Toast.LENGTH_SHORT).show();
                setLoginContent();
            }
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(getApplicationContext(),
                        "登录失败",
                        Toast.LENGTH_SHORT).show();
            }
            else if (type==2) {
                Toast.makeText(getApplicationContext(),
                        "发送验证码失败，更换邮箱或用户名再试一次",
                        Toast.LENGTH_SHORT).show();
            }
            else if (type==3) {
                Toast.makeText(getApplicationContext(),
                        "验证失败",
                        Toast.LENGTH_SHORT).show();
            }
            Log.e("LoginActivity---------",errorMsg);
        }
    }

    private void setLoginContent(){
        setContentView(R.layout.activity_main);
        setTitle("登录");
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        getcode = null;
        mailEdit = null;
        codeEdit = null;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                password = passwordEdit.getText().toString();
                if(password.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                HttpRequestManager http = HttpRequestManager.getInstance(getApplicationContext());
                MyCallBack callBack = new MyCallBack(1);
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("password", password);
                http.requestAsyn("api/user/login",1, data, callBack);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegisterContent();
            }
        });
    }

    private void setRegisterContent(){
        setContentView(R.layout.activity_register);
        setTitle("注册");

        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        mailEdit = findViewById(R.id.mail);
        codeEdit = findViewById(R.id.code);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        getcode = findViewById(R.id.get_code);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginContent();
            }
        });

        getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                password = passwordEdit.getText().toString();
                if(password.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mail = mailEdit.getText().toString();
                if(mail.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入邮箱",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                HttpRequestManager http = HttpRequestManager.getInstance(getApplicationContext());
                MyCallBack callBack = new MyCallBack(2);
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("password", password);
                data.put("mail", mail);
                http.requestAsyn("api/register",1, data, callBack);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                password = passwordEdit.getText().toString();
                if(password.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mail = mailEdit.getText().toString();
                if(mail.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入邮箱",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                checkcode = codeEdit.getText().toString();
                if(checkcode.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入验证码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                HttpRequestManager http = HttpRequestManager.getInstance(getApplicationContext());
                MyCallBack callBack = new MyCallBack(3);
                HashMap<String, String> data = new HashMap<>();
                data.put("mail", mail);
                data.put("verification", checkcode);
                http.requestAsyn("api/register/verify",1, data, callBack);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLoginContent();
    }
}