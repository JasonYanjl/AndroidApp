package com.example.frontend;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.adapter.RelationAdapter;
import com.example.frontend.info.RelationInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabRelationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabRelationFragment extends Fragment {

    int Type;

    Context context;

    RecyclerView recyclerView;

    RelationAdapter relationAdapter;

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
                LinkedList<RelationInfo> relationInfoLinkedList = new LinkedList<>();
                for(int i=0;i<nowList.size();i++) {
                    JSONObject tmpInfo = nowList.getJSONObject(i);
                    relationInfoLinkedList.addLast(new RelationInfo(tmpInfo.getInteger("userid"),
                            tmpInfo.getString("username"),
                            tmpInfo.getInteger("avatarid"),
                            tmpInfo.getString("avatarfilename"),
                            tmpInfo.getString("intro"),
                            tmpInfo.getInteger("subscribe"),
                            tmpInfo.getInteger("block")));
                }
                relationAdapter = new RelationAdapter(context, relationInfoLinkedList);
                recyclerView.setAdapter(relationAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            Log.i("TabRelation", result.toString());
        }


        @Override
        public void onReqFailed(String errorMsg) {
            if (type == 1) {

            }
            Log.e("TabRelation", errorMsg);
        }
    }

    public TabRelationFragment() {
        // Required empty public constructor
    }

    public static TabRelationFragment newInstance() {
        TabRelationFragment fragment = new TabRelationFragment();
        return fragment;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        recyclerView = view.findViewById(R.id.recycleViewRelation);

        if (Type == 0) {
            HashMap<String, String> data = new HashMap<>();
            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callback = new MyCallBack(1);
            data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
            http.requestAsyn("api/user/subscribelist", 0, data, callback);
        }
        else if (Type==1) {
            HashMap<String, String> data = new HashMap<>();
            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callback = new MyCallBack(1);
            data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
            http.requestAsyn("api/user/blocklist", 0, data, callback);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Type == 0) {
            HashMap<String, String> data = new HashMap<>();
            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callback = new MyCallBack(1);
            data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
            http.requestAsyn("api/user/subscribelist", 0, data, callback);
        }
        else if (Type==1) {
            HashMap<String, String> data = new HashMap<>();
            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callback = new MyCallBack(1);
            data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
            http.requestAsyn("api/user/blocklist", 0, data, callback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_relation, container, false);
    }
}