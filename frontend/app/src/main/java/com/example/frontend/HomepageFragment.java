package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.adapter.PostAdapter;
import com.example.frontend.adapter.RelationAdapter;
import com.example.frontend.info.PostInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    Context context;

    RadioButton radioButtonTime, radioButtonLike, radioButtonAll, radioButtonFollow;

    EditText searchText;

    RecyclerView recyclerView;

    PostAdapter postAdapter;

    public HomepageFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }

    public class MyCallBack implements HttpRequestManager.ReqCallBack {
        public int type;
        public MyCallBack(int type){
            this.type = type;
        }
        public void setType(int type){
            this.type = type;
        }
        @Override
        public void onReqSuccess(Object result) {
            if (type == 1) {
                JSONArray nowList = JSON.parseObject(result.toString()).getJSONArray("list");
                LinkedList<PostInfo> postInfoLinkedList = new LinkedList<>();
                for(int i=0;i<nowList.size();i++) {
                    JSONObject tmpInfo = nowList.getJSONObject(i);
                    postInfoLinkedList.addLast(new PostInfo(tmpInfo.getInteger("postid"),
                            tmpInfo.getInteger("userid"),
                            tmpInfo.getString("username"),
                            tmpInfo.getInteger("avatarid"),
                            tmpInfo.getString("avatarfilename"),
                            tmpInfo.getString("intro"),
                            tmpInfo.getInteger("fileid"),
                            tmpInfo.getString("filename"),
                            tmpInfo.getString("title"),
                            tmpInfo.getString("text"),
                            tmpInfo.getInteger("type"),
                            tmpInfo.getString("time"),
                            tmpInfo.getString("location"),
                            tmpInfo.getInteger("subscribe"),
                            tmpInfo.getInteger("block")));
                }
                postAdapter = new PostAdapter(context, postInfoLinkedList);
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            Log.i("HomepageRelation", result.toString());
        }


        @Override
        public void onReqFailed(String errorMsg) {
            if (type == 1) {

            }
            Log.e("HomepageRelation", errorMsg);
        }
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
        searchText = root.findViewById(R.id.search_view);
        radioButtonTime =  root.findViewById(R.id.radioButtonTime);
        radioButtonLike =  root.findViewById(R.id.radioButtonLike);
        radioButtonAll =  root.findViewById(R.id.radioButtonAll);
        radioButtonFollow =  root.findViewById(R.id.radioButtonFollow);
        recyclerView =  root.findViewById(R.id.recyclerViewHome);
        context = this.getActivity();

        UpdateRecyclerView();

        searchText.setOnClickListener(view -> {
            Log.i("click", "search click");
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        radioButtonTime.setOnClickListener(view -> {
            Log.i("click", "radioTime click");
            if (radioButtonLike.isChecked()) {
                radioButtonTime.setChecked(true);
                radioButtonLike.setChecked(false);
                UpdateRecyclerView();
            }
        });

        radioButtonLike.setOnClickListener(view -> {
            Log.i("click", "radioLike click");
            if (radioButtonTime.isChecked()) {
                radioButtonTime.setChecked(false);
                radioButtonLike.setChecked(true);
                UpdateRecyclerView();
            }
        });

        radioButtonAll.setOnClickListener(view -> {
            Log.i("click", "radioAll click");
            if (radioButtonFollow.isChecked()) {
                radioButtonAll.setChecked(true);
                radioButtonFollow.setChecked(false);
                UpdateRecyclerView();
            }
        });

        radioButtonFollow.setOnClickListener(view -> {
            Log.i("click", "radioFollow click");
            if (radioButtonAll.isChecked()) {
                radioButtonAll.setChecked(false);
                radioButtonFollow.setChecked(true);
                UpdateRecyclerView();
            }
        });

        return root;
    }

    private void UpdateRecyclerView() {
        HashMap<String, String> data = new HashMap<>();
        HttpRequestManager http = HttpRequestManager.getInstance(context);
        MyCallBack callback = new MyCallBack(1);
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        if (radioButtonTime.isChecked()) {
            data.put("sort", "time");
        }
        else {
            data.put("sort", "like");
        }
        if (radioButtonAll.isChecked()) {
            data.put("subscribe", Integer.toString(0));
        }
        else {
            data.put("subscribe", Integer.toString(1));
        }
        http.requestAsyn("api/discover/get", 0, data, callback);
    }
}