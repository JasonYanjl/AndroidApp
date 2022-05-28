package com.example.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    TabLayout tabLayout;

    TabRelationFragment childFragment;

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
        View root = inflater.inflate(R.layout.fragment_relation, container, false);

        tabLayout = root.findViewById(R.id.tablayout);

        childFragment = TabRelationFragment.newInstance();
        childFragment.setType(0);

        getChildFragmentManager().beginTransaction().replace(R.id.linearlayoutRelation,childFragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    childFragment = TabRelationFragment.newInstance();
                    childFragment.setType(0);
                }
                if (tab.getPosition() == 1){
                    childFragment = TabRelationFragment.newInstance();
                    childFragment.setType(1);
                }
                getChildFragmentManager().beginTransaction().replace(R.id.linearlayoutRelation,childFragment).commit();
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