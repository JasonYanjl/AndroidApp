package com.example.frontend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    private  static final int TAB_NUM = 2;

    @BindView(R.id.post_tab)
    protected TabLayout mtabLayout;
    @BindView(R.id.postLinearLayout)
    protected LinearLayout mLinearLayout;
    private PostFragmentEditor postFragmentEditor;

    private List<Fragment> mFragments;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance() {
        return new PostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, root);
        getChildFragmentManager().beginTransaction().replace(R.id.postLinearLayout,PostFragmentEditor.newInstance()).commit();
        mtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    getChildFragmentManager().beginTransaction().replace(R.id.postLinearLayout,PostFragmentEditor.newInstance()).commit();
                }
                else if (tab.getPosition() == 1){
                    getChildFragmentManager().beginTransaction().replace(R.id.postLinearLayout,DraftFragment.newInstance()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return root;
    }
}