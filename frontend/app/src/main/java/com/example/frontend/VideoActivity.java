package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        setTitle("视频详情");

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        assert tmpBar != null;
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        videoView = (VideoView) findViewById(R.id.videoView);

        Intent intent = getIntent();
        String fileAbsPath = intent.getStringExtra("fileAbsPath");

        videoView.setVideoPath(fileAbsPath);

        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();
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