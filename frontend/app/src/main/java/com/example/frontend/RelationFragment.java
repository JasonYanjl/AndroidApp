package com.example.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frontend.utils.TabRelationFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationFragment extends Fragment {

    TabLayout tabLayout;

    TabRelationFragment childFragment;

    public RelationFragment() {
        // Required empty public constructor
    }

    public static RelationFragment newInstance() {
        RelationFragment fragment = new RelationFragment();
        return fragment;
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

        tabLayout = (TabLayout)root.findViewById(R.id.tablayout);

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