package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    Context context;

    RadioButton radioButtonTitle, radioButtonText, radioButtonUsername;
    RadioButton radioButtonTypeAll, radioButtonType0, radioButtonType1, radioButtonType2, radioButtonType3;
    RadioButton radioButtonSortTime, radioButtonSortLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        setSearchContent();
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

    private void setSearchContent(){
        setContentView(R.layout.activity_search);
        setTitle("搜索");
        context = getApplicationContext();
        radioButtonTitle = findViewById(R.id.radioButtonTitle);
        radioButtonText = findViewById(R.id.radioButtonText);
        radioButtonUsername = findViewById(R.id.radioButtonUsername);
        radioButtonTypeAll = findViewById(R.id.radioButtonTypeAll);
        radioButtonType0 = findViewById(R.id.radioButtonType0);
        radioButtonType1 = findViewById(R.id.radioButtonType1);
        radioButtonType2 = findViewById(R.id.radioButtonType2);
        radioButtonType3 = findViewById(R.id.radioButtonType3);
        radioButtonSortTime = findViewById(R.id.radioButtonSortTime);
        radioButtonSortLike = findViewById(R.id.radioButtonSortLike);

        radioButtonTitle.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTitle.setChecked(true);
                radioButtonText.setChecked(false);
                radioButtonUsername.setChecked(false);
            }
        });
        radioButtonText.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTitle.setChecked(false);
                radioButtonText.setChecked(true);
                radioButtonUsername.setChecked(false);
            }
        });
        radioButtonUsername.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTitle.setChecked(false);
                radioButtonText.setChecked(false);
                radioButtonUsername.setChecked(true);
            }
        });
        radioButtonTypeAll.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTypeAll.setChecked(true);
                radioButtonType0.setChecked(false);
                radioButtonType1.setChecked(false);
                radioButtonType2.setChecked(false);
                radioButtonType3.setChecked(false);
            }
        });
        radioButtonType0.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTypeAll.setChecked(false);
                radioButtonType0.setChecked(true);
                radioButtonType1.setChecked(false);
                radioButtonType2.setChecked(false);
                radioButtonType3.setChecked(false);
            }
        });
        radioButtonType1.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTypeAll.setChecked(false);
                radioButtonType0.setChecked(false);
                radioButtonType1.setChecked(true);
                radioButtonType2.setChecked(false);
                radioButtonType3.setChecked(false);
            }
        });
        radioButtonType2.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTypeAll.setChecked(false);
                radioButtonType0.setChecked(false);
                radioButtonType1.setChecked(false);
                radioButtonType2.setChecked(true);
                radioButtonType3.setChecked(false);
            }
        });
        radioButtonType3.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {

                radioButtonTypeAll.setChecked(false);
                radioButtonType0.setChecked(false);
                radioButtonType1.setChecked(false);
                radioButtonType2.setChecked(false);
                radioButtonType3.setChecked(true);
            }
        });
        radioButtonSortTime.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                radioButtonSortTime.setChecked(true);
                radioButtonSortLike.setChecked(false);
            }
        });
        radioButtonSortLike.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                radioButtonSortTime.setChecked(false);
                radioButtonSortLike.setChecked(true);
            }
        });
    }
}