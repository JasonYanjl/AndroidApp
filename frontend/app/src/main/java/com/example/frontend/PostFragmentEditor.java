package com.example.frontend;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.frontend.info.UserInfo;
import com.example.frontend.recorder.Recorder;
import com.example.frontend.utils.FileManager;
import com.example.frontend.utils.HttpRequestManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;


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
    protected Button uploadBtn;
    @BindView(R.id.recordBtn)
    protected Button recordBtn;
    @BindView(R.id.post_savedraft_btn)
    protected Button saveDraft;

    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private Recorder recorder;
    private int postType = 0; //  0 text only, 1 with pic, 2 with audio, 3 with video
    private int mFileid = -1;
    final static String DRAFT_TITLE = "DRAFT_TITLE_POST_EDITOR";
    final static String DRAFT_CONTENT = "DRAFT_CONTENT_POST_EDITOR";
    final static String DRAFT_FILEID = "DRAFT_FILEID_POST_EDITOR";
    final static String DRAFT_POSTTYPE = "DRAFT_POSTTYPE_POST_EDITOR";
    private String contentStr = null;
    private String titleStr = null;
    private String draftName = null;
    public PostFragmentEditor() {
        // Required empty public constructor
    }


    public static PostFragmentEditor newInstance() {
        return new PostFragmentEditor();
    }
    static PostFragmentEditor newInstance(String s){
        PostFragmentEditor myFragment = new PostFragmentEditor();
        Bundle bundle = new Bundle();
        bundle.putString("DRAFT",s);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {

            titleStr = savedInstanceState.getString(DRAFT_TITLE);
            contentStr = savedInstanceState.getString(DRAFT_CONTENT);
            mFileid = savedInstanceState.getInt(DRAFT_FILEID);
            postType = savedInstanceState.getInt(DRAFT_POSTTYPE);
        }
    }


    @Override
    public void onPause() {
        Log.i("********************","ONPAUSE");
        try {
            String filepath = FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draftcache");
            outputStream = new FileOutputStream(new File(filepath));
            HashMap<String,String> data = new HashMap<>();
            JSONObject newdata= new JSONObject();
            newdata.put(DRAFT_TITLE,titleStr);
            newdata.put(DRAFT_CONTENT,contentStr);
            newdata.put(DRAFT_FILEID,mFileid);
            newdata.put(DRAFT_POSTTYPE,postType);
            Log.i("*****WRITE*****",newdata.toString());
            Log.i("*****WRITE*****PATH=",filepath);
            outputStream.write(newdata.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("********************","ONCREATE");
        Bundle bundle = getArguments();
        if(bundle!=null)
            draftName = bundle.getString("DRAFT");

        View root = inflater.inflate(R.layout.fragment_post_editor, container, false);
        ButterKnife.bind(this, root);
        try {
            String filepath;
            if(draftName == null)
            filepath = FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draftcache");
            else filepath = FileManager.getInstance()
                        .getUserFileAbsolutePath(requireContext(), "Draft/"+draftName);

            inputStream = new FileInputStream(new File(filepath));
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder ret = new StringBuilder();
            String temp;
            while(null !=(temp = bufferedReader.readLine())){
                Log.i("***APPDEND***",temp);
                ret.append(temp);
            }
            JSONObject newdata= JSONObject.parseObject(ret.toString());
            Log.i("*****READ*****PATH=",filepath);
            if(newdata!=null) {
                Log.i("*****READ*****",newdata.toString());
                titleStr = newdata.getString(DRAFT_TITLE);
                contentStr = newdata.getString(DRAFT_CONTENT);
                mFileid = newdata.getInteger(DRAFT_FILEID);
                postType = newdata.getInteger(DRAFT_POSTTYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        Refresh();
        textPosition.setOnClickListener(view -> AddPosition());
        uploadBtn.setOnClickListener(view -> ChooseFileType());
        saveDraft.setOnClickListener(view -> SaveAsDraft());
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleStr = titleEdit.getText().toString();
            }
        });
        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                contentStr = contentEdit.getText().toString();
            }
        });
        recorder = Recorder.getInstance(getContext());
        submitBtn.setOnClickListener(view -> Submit());
        recordBtn.setOnTouchListener(new View.OnTouchListener(){
            private String time_before;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) { // 按下

                    recorder.setChatid("voice");
                    recorder.record();
                    time_before = Calendar.getInstance().getTime().toString();
                    Log.e("beforetime",time_before);
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    String time_end = Calendar.getInstance().getTime().toString();
                    Log.e("endtime", time_end);
                    if(time_before.equals(time_end)) {
                        recorder.setRecording(false);
                        Toast.makeText(getActivity(),
                                "录音失败",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    String musicname = null;
                    try {
                        musicname = recorder.stopRecord();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("musicname:",musicname);
                    File file=new File(musicname);

                    HttpRequestManager manager = HttpRequestManager.getInstance(getContext());
                    MyCallBack callback = new MyCallBack(1);
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("file",file);
                    data.put("userid", UserInfo.getInstance().getUserid());
                    data.put("type", Integer.toString(2));
                    manager.upLoadFile("api/file/upload", data, callback);
                }
                return false;

            }
        });
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
                mFileid= res.getInteger("fileid");
                // submit post here
            }
            Log.i("PostActivity---------",result.toString());
            Refresh();
        }

        @Override
        public void onReqFailed(String errorMsg) {
            Log.e("PostActivity---------",errorMsg);
            Refresh();
        }
    }
    public void Submit(){

        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();
        if(title.length() == 0 || content.length() == 0)
        {
            Toast.makeText(getActivity(),
                    "不能发送空内容",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        HttpRequestManager http = HttpRequestManager.getInstance(requireActivity().getApplicationContext());
        PostFragmentEditor.MyCallBack callBack = new MyCallBack(0);
        HashMap<String, String> data = new HashMap<>();
        data.put("userid", Integer.toString(UserInfo.getInstance().getUserid()));
        data.put("title",title);
        data.put("text",content);
        data.put("type",String.valueOf(postType));
        data.put("fileid",String.valueOf(mFileid));
        data.put("location","");
        Log.i("DATA IS",data.toString());
        http.requestAsyn("api/discover/post",HttpRequestManager.TYPE_POST_JSON,data,callBack);

    }
    public void ChooseFileType(){
        recordBtn.setVisibility(View.INVISIBLE);
        PopupMenu popup = new PopupMenu(getContext(), uploadBtn);
        popup.setOnMenuItemClickListener(item->{
            Intent intent;
                switch (item.getItemId()) {
                    case R.id.uploadImage:
                        this.postType = 1;
                        checkPermission(1);
                        intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,0x000101);
                        break;
                    case R.id.uploadAudio:
                        this.postType = 2;
                        recordBtn.setVisibility(View.VISIBLE);
                        break;
                    case R.id.uploadVideo:
                        this.postType = 3;
                        // TODO： fix according to https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
                        Log.i("click", "Select Video");
                        checkPermission(3);
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,0x000102);
                        break;
                    default:
            }
            return true;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.addfile, popup.getMenu());

        popup.show();
    }
    private void ClearFileInfo(){
        recordBtn.setVisibility(View.INVISIBLE);
        postType = 0;
        mFileid = -1;
        this.Refresh();
    }
    private void checkPermission(int type) {
        // first we need check this Drive has? CAMERA Permission
        Context context = requireContext();
        if(type == 1 || type == 3) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                return;
            }
        }
        if(type == 2){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }
    private void GetPermission() {
        Context context = requireContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED  ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED  ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)

        {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
    private void Refresh(){
        if(postType != 2)
            recordBtn.setVisibility(View.INVISIBLE);
        if(mFileid == -1) {
            uploadBtn.setOnClickListener(view -> ChooseFileType());
            uploadBtn.setText("添加附件");
        } else {
            uploadBtn.setOnClickListener(view -> ClearFileInfo());
            uploadBtn.setText("删除附件"+ mFileid);
        }
        if(titleStr!=null)
            titleEdit.setText(titleStr);
        if(contentStr!=null)
            contentEdit.setText(contentStr);
    }

    public void AddPosition(){

        // TODO: use getlocation api instead
        String mlocation = textPosition.getText().toString();

        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + mlocation);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);

        // Start the activity.
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", Integer.toString(requestCode));
        if(requestCode == 0x000101 || requestCode == 0x000102){
            if (resultCode == Activity.RESULT_OK) {
                //判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4及以上系统使用这个方法处理图片
                    //TODO: send http requests
                    String imagePath = "";
                    Uri uri = data.getData();
                    Log.i("Author",uri.getAuthority());
                    if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            Log.i("type","com.android.providers.media.documents");
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        }
                        else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            Log.i("type","com.android.providers.downloads.documents");
                            Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        }
                        else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            Log.i("type","file");
                            imagePath = uri.getPath();
                        }
                    }
                    else if ("content".equalsIgnoreCase(uri.getScheme())) {
                        Log.i("type","content");
                        imagePath = getImagePath(uri, null);
                    }
                    File nowAvatar = new File(imagePath);
                    HttpRequestManager http = HttpRequestManager.getInstance(requireContext());
                    MyCallBack callback = new MyCallBack(1);
                    HashMap<String, Object> photoData = new HashMap<>();
                    photoData.put("file", nowAvatar);
                    photoData.put("userid", UserInfo.getInstance().getUserid());
                    if(requestCode == 0x000102) photoData.put("type", Integer.toString(3));
                    if(requestCode == 0x000101) photoData.put("type", Integer.toString(1));
                    http.upLoadFile("api/file/upload", photoData, callback);
                }
            }
        }
        else {

        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = requireContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                // wrong path
                if (index < 0) return null;
                path = cursor.getString(index);
            }

            cursor.close();
        }
        return path;
    }
    private void SaveAsDraft(){
        try {
            Date now = Calendar.getInstance().getTime();
            String filepath ;
            if(draftName != null)
                filepath=FileManager.getInstance()
                        .getUserFileAbsolutePath(requireContext(), "Draft/"+draftName);
            else
                filepath= FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draft/"+now.toString());

            outputStream = new FileOutputStream(new File(filepath));
            HashMap<String,String> data = new HashMap<>();
            JSONObject newdata= new JSONObject();
            newdata.put(DRAFT_TITLE,titleStr);
            newdata.put(DRAFT_CONTENT,contentStr);
            newdata.put(DRAFT_FILEID,mFileid);
            newdata.put(DRAFT_POSTTYPE,postType);
            outputStream.write(newdata.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}