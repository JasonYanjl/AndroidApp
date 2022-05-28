package com.example.frontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    Context context;

    RadioButton radioButtonTitle, radioButtonText, radioButtonUsername;
    RadioButton radioButtonTypeAll, radioButtonType0, radioButtonType1, radioButtonType2, radioButtonType3;
    RadioButton radioButtonSortTime, radioButtonSortLike;

    EditText editTextSearch;
    Button buttonButton;
    RecyclerView recyclerViewSearchResult;

    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set back button
        ActionBar tmpBar = getSupportActionBar();
        assert tmpBar != null;
        tmpBar.setHomeButtonEnabled(true);
        tmpBar.setDisplayShowHomeEnabled(true);
        tmpBar.setDisplayHomeAsUpEnabled(true);

        searchText = "";
        setTitle("搜索");
        context = getApplicationContext();

        setSearchChoice();
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
        setContentView(R.layout.activity_search_result);
        radioButtonTitle = null;
        radioButtonText = null;
        radioButtonUsername = null;
        radioButtonTypeAll = null;
        radioButtonType0 = null;
        radioButtonType1 = null;
        radioButtonType2 = null;
        radioButtonType3 = null;
        radioButtonSortTime = null;
        radioButtonSortLike = null;

        editTextSearch = findViewById(R.id.editTextSearchResult);
        buttonButton = null;
        recyclerViewSearchResult = findViewById(R.id.recyclerViewSearchResult);

        editTextSearch.setText(searchText);

        editTextSearch.setOnClickListener(v -> setSearchChoice());
    }

    private void setSearchChoice(){
        setContentView(R.layout.activity_search);
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

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonButton = findViewById(R.id.buttonSearch);
        recyclerViewSearchResult = null;

        buttonButton.setOnClickListener(v -> {
            searchText = editTextSearch.getText().toString();
            setSearchContent();
        });

        editTextSearch.setText(searchText);

        radioButtonTitle.setOnClickListener(view -> {

            radioButtonTitle.setChecked(true);
            radioButtonText.setChecked(false);
            radioButtonUsername.setChecked(false);
        });
        radioButtonText.setOnClickListener(view -> {

            radioButtonTitle.setChecked(false);
            radioButtonText.setChecked(true);
            radioButtonUsername.setChecked(false);
        });
        radioButtonUsername.setOnClickListener(view -> {

            radioButtonTitle.setChecked(false);
            radioButtonText.setChecked(false);
            radioButtonUsername.setChecked(true);
        });
        radioButtonTypeAll.setOnClickListener(view -> {

            radioButtonTypeAll.setChecked(true);
            radioButtonType0.setChecked(false);
            radioButtonType1.setChecked(false);
            radioButtonType2.setChecked(false);
            radioButtonType3.setChecked(false);
        });
        radioButtonType0.setOnClickListener(view -> {

            radioButtonTypeAll.setChecked(false);
            radioButtonType0.setChecked(true);
            radioButtonType1.setChecked(false);
            radioButtonType2.setChecked(false);
            radioButtonType3.setChecked(false);
        });
        radioButtonType1.setOnClickListener(view -> {

            radioButtonTypeAll.setChecked(false);
            radioButtonType0.setChecked(false);
            radioButtonType1.setChecked(true);
            radioButtonType2.setChecked(false);
            radioButtonType3.setChecked(false);
        });
        radioButtonType2.setOnClickListener(view -> {

            radioButtonTypeAll.setChecked(false);
            radioButtonType0.setChecked(false);
            radioButtonType1.setChecked(false);
            radioButtonType2.setChecked(true);
            radioButtonType3.setChecked(false);
        });
        radioButtonType3.setOnClickListener(view -> {

            radioButtonTypeAll.setChecked(false);
            radioButtonType0.setChecked(false);
            radioButtonType1.setChecked(false);
            radioButtonType2.setChecked(false);
            radioButtonType3.setChecked(true);
        });
        radioButtonSortTime.setOnClickListener(view -> {
            radioButtonSortTime.setChecked(true);
            radioButtonSortLike.setChecked(false);
        });
        radioButtonSortLike.setOnClickListener(view -> {
            radioButtonSortTime.setChecked(false);
            radioButtonSortLike.setChecked(true);
        });
    }
}