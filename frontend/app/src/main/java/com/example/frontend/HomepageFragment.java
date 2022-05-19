package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    Context context;

    RadioButton radioButtonTime, radioButtonLike, radioButtonAll, radioButtonFollow;

    EditText searchText;

    public HomepageFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance() {
        HomepageFragment fragment = new HomepageFragment();
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
        View root = inflater.inflate(R.layout.fragment_homepage, container, false);
        searchText = (EditText) root.findViewById(R.id.search_view);
        radioButtonTime = (RadioButton) root.findViewById(R.id.radioButtonTime);
        radioButtonLike = (RadioButton) root.findViewById(R.id.radioButtonLike);
        radioButtonAll = (RadioButton) root.findViewById(R.id.radioButtonAll);
        radioButtonFollow = (RadioButton) root.findViewById(R.id.radioButtonFollow);
        context = this.getActivity();

        searchText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("click", "search click");

            }
        });

        radioButtonTime.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("click", "radioTime click");
                if (radioButtonLike.isChecked()) {
                    radioButtonTime.setChecked(true);
                    radioButtonLike.setChecked(false);
                }
            }
        });

        radioButtonLike.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("click", "radioLike click");
                if (radioButtonTime.isChecked()) {
                    radioButtonTime.setChecked(false);
                    radioButtonLike.setChecked(true);
                }
            }
        });

        radioButtonAll.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("click", "radioAll click");
                if (radioButtonFollow.isChecked()) {
                    radioButtonAll.setChecked(true);
                    radioButtonFollow.setChecked(false);
                }
            }
        });

        radioButtonFollow.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("click", "radioFollow click");
                if (radioButtonAll.isChecked()) {
                    radioButtonAll.setChecked(false);
                    radioButtonFollow.setChecked(true);
                }
            }
        });

        return root;
    }
}