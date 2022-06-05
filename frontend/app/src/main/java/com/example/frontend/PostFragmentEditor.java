package com.example.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.entity.chat.User;
import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.HttpRequestManager;

import java.io.File;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragmentEditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragmentEditor extends Fragment {
    @BindView(R.id.textPostTitle)
    protected EditText titleEdit;
    @BindView(R.id.textPostContent)
    protected EditText contentEdit;
    @BindView(R.id.post_submit_btn)
    protected Button submitBtn;

    private int postType = 0; //  0 text only, 1 with pic, 2 with audio, 3 with video
    private File mfile;
    public PostFragmentEditor() {
        // Required empty public constructor
    }

    public static PostFragmentEditor newInstance() {
        return new PostFragmentEditor();
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
        return root;
    }
    private static class MyCallBack implements HttpRequestManager.ReqCallBack {
        public int type;
        public MyCallBack(int type){
            this.type = type;
        } // 0 for submit, 1 for upload
        public void setType(int type){
            this.type = type;
        }
        @Override

        public void onReqSuccess(Object result) {
            if(type == 0) {
                // submit callback
                JSONArray nowList = JSON.parseObject(result.toString()).getJSONArray("list");
            }
            if(type == 1){
                // upload callback
                JSONObject res = JSON.parseObject(result.toString());
                int fileid = res.getInteger("fileid");
                // submit post here
            }
            Log.i("PostActivity---------",result.toString());
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("PostActivity---------",errorMsg);
        }
    }
    public void Submit(){
        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();
        HttpRequestManager http = HttpRequestManager.getInstance(requireActivity().getApplicationContext());
        PostFragmentEditor.MyCallBack callBack = new MyCallBack(0);
        HashMap<String, String> data = new HashMap<>();
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        data.put("title",title);
        data.put("text",content);
        data.put("type",String.valueOf(postType));
        if(postType != 0){
            this.UploadFile();
        }

    }

    public void UploadFile(){
        int fileid = -1;
        try{
            HttpRequestManager http = HttpRequestManager.getInstance(requireActivity().getApplicationContext());
            PostFragmentEditor.MyCallBack callBack = new MyCallBack(1);
            HashMap<String, Object> fileData = new HashMap<>();
            fileData.put("file", mfile);
            fileData.put("userid", UserInfo.getInstance().getUserid());
            fileData.put("type",String.valueOf(postType));
            http.upLoadFile("api/file/upload",fileData,callBack);
        }catch (Exception e){
            Log.e("PostActivity---------",e.toString());
        }
    }
}