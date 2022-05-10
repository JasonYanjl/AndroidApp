package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.entity.chat.User;
import com.example.frontend.entity.chat.Message;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FrontpageActivity extends AppCompatActivity {

    private Fragment homepageFragment, relationFragment, postFragment, accountFragment;

    @BindView(R.id.navigation)
    BottomNavigationView navigationMenu;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mTimeCounterRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("通知轮询", "+1");

            updateMessage();

            mHandler.postDelayed(this, 2 * 1000);
        }
    };

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
                JSONObject res = JSON.parseObject(result);
                JSONArray resList = res.getJSONArray("list");

                if (resList.size() > 0) {
                    if (UserInfo.getInstance().messageList.size() < resList.size()) {
                        User user = new User("0", "", "null", false);
                        Integer mListSize = UserInfo.getInstance().messageList.size();
                        for(int i = resList.size() - mListSize - 1; i >= 0; i--) {
                            String tmpContent = resList.getJSONObject(i).getString("content");
                            String tmpTime = resList.getJSONObject(i).getString("time");
                            Integer Year = Integer.valueOf(tmpTime.substring(0, 4));
                            Integer Month = Integer.valueOf(tmpTime.substring(5, 7));
                            Integer Day = Integer.valueOf(tmpTime.substring(8, 10));
                            Integer Hour = Integer.valueOf(tmpTime.substring(11, 13));
                            Integer Minute = Integer.valueOf(tmpTime.substring(14, 16));
                            Integer Second = Integer.valueOf(tmpTime.substring(17, 19));
                            Date tmpDate = new Date(Year - 1900, Month - 1, Day, Hour, Minute, Second);

                            Message message = new Message("1", user, tmpContent, tmpDate, true);
                            UserInfo.getInstance().addMessage(message);
                        }
                    }
                }
            }
            Log.i("FrontpageActivity---",result);
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {

            }
            Log.e("FrontpageActivity---",errorMsg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        setTitle("校园论坛");

        ButterKnife.bind(this);

        homepageFragment = HomepageFragment.newInstance();
        relationFragment = RelationFragment.newInstance();
        postFragment = PostFragment.newInstance();
        accountFragment = AccountFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,homepageFragment).commit();

        navigationMenu.setOnNavigationItemSelectedListener(item -> {
            homepageFragment = HomepageFragment.newInstance();
            relationFragment = RelationFragment.newInstance();
            postFragment = PostFragment.newInstance();
            accountFragment = AccountFragment.newInstance();
            switch (item.getItemId()) {
                case R.id.homepage:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,homepageFragment).commit();
                    return true;
                case R.id.relation:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,relationFragment).commit();
                    return true;
                case R.id.post:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,postFragment).commit();
                    return true;
                case R.id.account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,accountFragment).commit();
                    return true;
            }
            return false;
        });
        mTimeCounterRunnable.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTimeCounterRunnable);
    }

    private synchronized void updateMessage() {
        HttpRequestManager http = HttpRequestManager.getInstance(getApplicationContext());
        MyCallBack callBack = new MyCallBack(1);
        HashMap<String, String> data = new HashMap<>();
        data.put("senderid", Integer.toString(1));
        data.put("receiverid", Integer.toString(UserInfo.getInstance().getUserid()));
        http.requestAsyn("api/chat/get",0, data, callBack);
    }
}