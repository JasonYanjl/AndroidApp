package com.example.frontend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

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
    @BindView(R.id.textPosition)
    protected TextView textPosition;
    @BindView(R.id.post_addtion_btn)
    protected ImageButton uploadBtn;


    private String mlocation;

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
        View root = inflater.inflate(R.layout.fragment_post_editor, container, false);
        ButterKnife.bind(this, root);
        textPosition.setOnClickListener(view -> {AddPosition();});
        uploadBtn.setOnClickListener(view -> {OnClickUpload();});
        return root;
    }
    private class MyCallBack implements HttpRequestManager.ReqCallBack {
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
        if(postType != 0){
            this.UploadFile();
            return;
        }

        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();
        HttpRequestManager http = HttpRequestManager.getInstance(requireActivity().getApplicationContext());
        PostFragmentEditor.MyCallBack callBack = new MyCallBack(0);
        HashMap<String, String> data = new HashMap<>();
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        data.put("title",title);
        data.put("text",content);
        data.put("type",String.valueOf(postType));


    }
    public void OnClickUpload(){
        PopupMenu popup = new PopupMenu(getContext(), uploadBtn);
        popup.setOnMenuItemClickListener(item->{
                switch (item.getItemId()) {
                    case R.id.uploadImage:
                        this.postType = 1;
                        break;
                    case R.id.uploadVideo:
                        this.postType = 3;
                        break;
                    case R.id.uploadAudio:
                        this.postType = 2;
                        break;
                    default:
            }
            SelectFile();
            return true;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.addfile, popup.getMenu());

        popup.show();
    }
    private void checkPermission(int type) {
        // first we need check this Drive has? CAMERA Permission
        if(type == 1 || type == 3) {
            Context context = getContext();
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                return;
            }
        }
        if(type == 2){
            // TODO: check audio permission here
        }

    }
    public void SelectFile(){
        if(postType == 1){
            Log.i("click", "Select Picture");

            checkPermission(1);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0x000101);
            return;
        }
        if(postType == 2){
            // audio
            return;
        }
        if(postType == 3){
            // video
            // TODO： fix according to https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
            Log.i("click", "Select Video");

            checkPermission(3);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0x000101);
            return;
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
    public void AddPosition(){
        // add position via 3rd-party app
        // TODO: use getlocation api instead
        mlocation = textPosition.getText().toString();

        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + mlocation);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);

        // Start the activity.
        startActivity(intent);
    }
    public void onChangeTitleSaver(){
        String title = titleEdit.getText().toString();
    }
    public void onChangeContentSaver(){
        String content = contentEdit.getText().toString();
    }

}