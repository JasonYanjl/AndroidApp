package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FrontpageActivity extends AppCompatActivity {

    private Fragment homepageFragment, relationFragment, postFragment, messageFragment, accountFragment;

    @BindView(R.id.navigation)
    BottomNavigationView navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        setTitle("校园论坛");

        ButterKnife.bind(this);

        homepageFragment = HomepageFragment.newInstance();
        relationFragment = RelationFragment.newInstance();
        postFragment = PostFragment.newInstance();
        messageFragment = MessageFragment.newInstance();
        accountFragment = AccountFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,homepageFragment).commit();

        navigationMenu.setOnNavigationItemSelectedListener(item -> {
            homepageFragment = HomepageFragment.newInstance();
            relationFragment = RelationFragment.newInstance();
            postFragment = PostFragment.newInstance();
            messageFragment = MessageFragment.newInstance();
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
                case R.id.message:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,messageFragment).commit();
                    return true;
                case R.id.account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,accountFragment).commit();
                    return true;
            }
            return false;
        });
    }
}