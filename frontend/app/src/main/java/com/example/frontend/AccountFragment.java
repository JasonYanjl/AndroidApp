package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.adapter.PostAdapter;
import com.example.frontend.info.PostInfo;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    private Context context;

    FloatingActionButton fab;

    TextView textViewUsername, textViewIntro;

    ImageView imageViewAvatar;

    ImageButton imageButtonSettings;

    RecyclerView recyclerView;

    PostAdapter postAdapter;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
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
                JSONObject res = JSON.parseObject(result.toString());
                String filename = res.getString("filename");
                UserInfo.getInstance().setAvatarFilename(filename);

                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + filename;

                if (FileManager.getInstance().getUserFileExists(context, "avatar" + "/" + filename)) {
                    // set avatar
                    imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
                }
                else {
                    String url = HttpRequestManager.getInstance(context).getBaseUrl() + "/api/file/download?fileid="
                            + Integer.toString(UserInfo.getInstance().getAvatarid());
                    String destDir = FileManager.getInstance().getUserFileAbsolutePath(context, "avatar");
                    MyCallBack callback = new MyCallBack(2);
                    HttpRequestManager.getInstance(context).downLoadFile(url,filename,destDir,callback);
                    Log.i("Download to", fileAbsPath);
                }
            }
            else if (type==2) {
                String fileAbsPath =  FileManager.getInstance().getUserFileAbsolutePath(context, "avatar")
                        + "/" + UserInfo.getInstance().getAvatarFilename();
                imageViewAvatar.setImageDrawable(Drawable.createFromPath(fileAbsPath));
            }
            else if (type==16) {
                JSONArray nowList = JSON.parseObject(result.toString()).getJSONArray("list");
                LinkedList<PostInfo> postInfoLinkedList = new LinkedList<>();
                for(int i = 0; i < nowList.size(); i++) {
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
            Log.i("AccountFrag---",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            if (type==1) {
                Toast.makeText(context,
                        "??????????????????",
                        Toast.LENGTH_SHORT).show();
                imageViewAvatar.setImageResource(R.drawable.ic_avatar);
            }
            else if (type==2) {
                Toast.makeText(context,
                        "??????????????????",
                        Toast.LENGTH_SHORT).show();
                imageViewAvatar.setImageResource(R.drawable.ic_avatar);
            }
            Log.e("AccountFrag---",errorMsg);
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
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        fab = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);
        textViewUsername = (TextView) root.findViewById(R.id.textViewUsername);
        textViewIntro = (TextView) root.findViewById(R.id.textViewIntro);
        imageViewAvatar = (ImageView) root.findViewById(R.id.imageViewAvatar);
        imageButtonSettings = (ImageButton) root.findViewById(R.id.buttonSetting);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerViewAccount);
        context = this.getActivity();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click", "fab click");

                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });

        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "imageButton click");
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        // set avatar username intro
        setInfo();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setInfo();
    }

    public void setInfo() {
        textViewUsername.setText(UserInfo.getInstance().getUsername());
        textViewIntro.setText(UserInfo.getInstance().getIntro());
        if (UserInfo.getInstance().getAvatarid() != -1) {
            Log.i("Download", "Avatar");

            HttpRequestManager http = HttpRequestManager.getInstance(context);
            MyCallBack callBack = new MyCallBack(1);
            HashMap<String, String> data = new HashMap<>();
            data.put("fileid", Integer.toString(UserInfo.getInstance().getAvatarid()));
            http.requestAsyn("api/file/filename",0, data, callBack);
        }
        else {
            imageViewAvatar.setImageResource(R.drawable.ic_avatar);
        }

        HttpRequestManager http = HttpRequestManager.getInstance(context);
        MyCallBack callBack = new MyCallBack(16);
        HashMap<String, String> data = new HashMap<>();
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        data.put("posterid", Integer.toString(UserInfo.getInstance().getUserid()));
        http.requestAsyn("api/discover/allpost",0, data, callBack);
    }

}