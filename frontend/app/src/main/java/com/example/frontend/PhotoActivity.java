package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.chrisbanes.photoview.PhotoView;

public class PhotoActivity extends AppCompatActivity {

    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("图片详情");
        // set back button
        ActionBar tmpBar = getSupportActionBar();
        assert tmpBar != null;
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        photoView = findViewById(R.id.photo_view);

        Intent intent = getIntent();
        String fileAbsPath = intent.getStringExtra("fileAbsPath");

        photoView.setImageDrawable(Drawable.createFromPath(fileAbsPath));
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