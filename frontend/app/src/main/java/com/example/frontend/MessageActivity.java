package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.frontend.entity.chat.Message;
import com.example.frontend.info.UserInfo;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity implements DateFormatter.Formatter {
    @BindView(R.id.messagesList)
    MessagesList messagesList;

    private MessagesListAdapter messagesAdapter;
    ArrayList<Message> arrayList;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mTimeCounterRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("更新通知页面", "+1");
            mHandler.postDelayed(this, 3 * 1000); // 3 seconds
            refreshMessage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setTitle("通知");
        ButterKnife.bind(this);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();

        messagesAdapter = new MessagesListAdapter<>("1", null);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(messagesAdapter);

        refreshMessage();

        mTimeCounterRunnable.run();
    }

    private synchronized void refreshMessage() {
        messagesAdapter.clear();
        arrayList.clear();
        arrayList.addAll(UserInfo.getInstance().getMessageList());
        messagesAdapter.addToEnd(arrayList, true);
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return "今天";
        } else if (DateFormatter.isYesterday(date)) {
            return "昨天";
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTimeCounterRunnable);
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