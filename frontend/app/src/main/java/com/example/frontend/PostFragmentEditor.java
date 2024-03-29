package com.example.frontend;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageView;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Ref;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


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
    @BindView(R.id.post_discard_btn)
    protected Button discardButton;

    Context context;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private Recorder recorder;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String provider;
    private int postType = 0; //  0 text only, 1 with pic, 2 with audio, 3 with video
    private int mFileid = -1;
    Uri imageUri;
    private String mlocation = "";
    final static String DRAFT_TITLE = "DRAFT_TITLE_POST_EDITOR";
    final static String DRAFT_CONTENT = "DRAFT_CONTENT_POST_EDITOR";
    final static String DRAFT_FILEID = "DRAFT_FILEID_POST_EDITOR";
    final static String DRAFT_POSTTYPE = "DRAFT_POSTTYPE_POST_EDITOR";
    final static String DRAFT_LOCATION = "DRAFT_LOCATION";
    private String contentStr = null;
    private String titleStr = null;
    private String draftName = null;
    String path=null;
    public PostFragmentEditor() {
        // Required empty public constructor
    }


    public static PostFragmentEditor newInstance() {
        return new PostFragmentEditor();
    }
    static PostFragmentEditor newInstance(String s){
        if(s == "自动保存")
            return new PostFragmentEditor();
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
            mlocation = savedInstanceState.getString(DRAFT_LOCATION);
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
            newdata.put(DRAFT_LOCATION, mlocation);
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
        context =   getActivity();
        GetPermission();
        //
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
        criteria.setAltitudeRequired(false);// 不要求海拔
        criteria.setBearingRequired(false);// 不要求方位
        criteria.setCostAllowed(true);// 允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
        provider = locationManager.getBestProvider(criteria, true);
        locationListener = new LocationListener() {
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }
            public void onLocationChanged(Location location) {
                Double lat = location.getLatitude();
                Double lon = location.getLongitude();
                Log.e("android_lat", String.valueOf(lat));
                Log.e("android_lon", String.valueOf(lon));
            }
        };
        // 监听位置变化，2秒一次，距离10米以上
        locationManager.requestLocationUpdates(provider, 500, 10,
                locationListener);

        // Inflate the layout for this fragment
        Log.i("********************","ONCREATE");
        Bundle bundle = getArguments();
        if(bundle!=null) {
            draftName = bundle.getString("DRAFT");
            Log.e("****DRAFT NAME IS ****",draftName);
        }
        else
            Log.e("****DRAFT NAME IS ****","empty");
        View root = inflater.inflate(R.layout.fragment_post_editor, container, false);
        ButterKnife.bind(this, root);
        try {
            String filepath;
            if(draftName == null) {
                filepath = FileManager.getInstance()
                        .getUserFileAbsolutePath(requireContext(), "Draftcache");
            }
            else {filepath = FileManager.getInstance()
                        .getUserFileAbsolutePath(requireContext(), "Draft/"+draftName);}

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
                mlocation = newdata.getString(DRAFT_LOCATION);
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
        saveDraft.setOnClickListener(view -> SaveAsDraft());
        discardButton.setOnClickListener(view -> DiscardAllChanges());
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
                if(draftName != null) {
                    String filepath;
                    filepath = FileManager.getInstance()
                            .getUserFileAbsolutePath(requireContext(), "Draft/" + draftName);
                    FileManager.getInstance().deleteFile(filepath);
                }
                Toast.makeText(getActivity(),
                        "发送成功",
                        Toast.LENGTH_SHORT).show();
                titleEdit.setText("");
                contentEdit.setText("");
                postType = 0;
                mFileid = -1;
                mlocation = "";
                Refresh();

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
        data.put("location",mlocation);
        Log.i("DATA IS",data.toString());
        http.requestAsyn("api/discover/post",HttpRequestManager.TYPE_POST_JSON,data,callBack);

    }
    public void ChooseFileType(){
        recordBtn.setVisibility(View.INVISIBLE);
        GetPermission();
        PopupMenu popup = new PopupMenu(getContext(), uploadBtn);
        popup.setOnMenuItemClickListener(item->{
            Intent intent;
                switch (item.getItemId()) {
                    case R.id.uploadImage:
                        this.postType = 1;
                        checkPermission(1);
//                        intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(intent,0x000101);
                        File output = new File(context.getExternalCacheDir(), "output.jpg");

                        Objects.requireNonNull(output.getParentFile()).mkdirs();
                        try {
                            if (output.exists()){
                                output.delete();
                            }
                            output.createNewFile();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT>=24)
                            //判断安卓的版本是否高于7.0，高于则调用高于的方法，低于则调用低于的方法
                            //把文件转换成Uri对象
                    /*
                    因为android7.0以后直接使用本地真实路径是不安全的，会抛出异常。
                    FileProvider是一种特殊的内容提供器，可以对数据进行保护
                     */
                        {
                            imageUri= FileProvider.getUriForFile(getActivity(),
                                    "com.buildmaterialapplication.fileprovider",output);
                            //对应Mainfest中的provider
//            imageUri=Uri.fromFile(outputImage);
                            path=imageUri.getPath();
                            Log.e(">7:",path);
                        }
                        else {
                            imageUri= Uri.fromFile(output);
                            path=imageUri.getPath();

                            Log.e("<7:",imageUri.getPath());

                        }
                        intent=new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(intent,0x000103);
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
//                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(intent,0x000102);

                        intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        //设置视频录制的最长时间
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                        //设置视频录制的画质
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(intent, 0x000104);
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
        if (!mlocation.equals("")) {
            textPosition.setText("经纬度："+ mlocation);
        }
        else {
            textPosition.setText("添加位置");
        }
        if(titleStr!=null)
            titleEdit.setText(titleStr);
        if(contentStr!=null)
            contentEdit.setText(contentStr);
    }

    public void AddPosition(){

        // TODO: use getlocation api instead
        GetPermission();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.i("经度:", Double.toString(location.getLongitude()));
                Log.i("纬度:", Double.toString(location.getLatitude()));
                String str = location.getLatitude() + "," + location.getLongitude();
                mlocation = str;
                Refresh();
            } else {
                Log.e("error", "空指针");
            }
        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }

            cursor.close();
        }
        return path;
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", Integer.toString(requestCode));
        if(requestCode == 0x000101){
            if (resultCode == Activity.RESULT_OK) {
                //判断手机系统版本号
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
                        imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,false);
                    }
                    else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                        Log.i("type","com.android.providers.downloads.documents");
                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(docId));
                        imagePath = getImagePath(contentUri, null,false);
                    }
                    else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        Log.i("type","file");
                        imagePath = uri.getPath();
                    }
                }
                else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    Log.i("type","content");
                    imagePath = getImagePath(uri, null,false);
                }
                File nowAvatar = new File(imagePath);
                HttpRequestManager http = HttpRequestManager.getInstance(requireContext());
                MyCallBack callback = new MyCallBack(1);
                HashMap<String, Object> photoData = new HashMap<>();
                photoData.put("file", nowAvatar);
                photoData.put("userid", UserInfo.getInstance().getUserid());
                photoData.put("type", Integer.toString(1));
                http.upLoadFile("api/file/upload", photoData, callback);
            }
        }
        else if(requestCode == 0x000102) {
            if (resultCode == Activity.RESULT_OK) {
                //判断手机系统版本号
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
                        String selection = MediaStore.Video.Media._ID + "=" + id;
                        imagePath = getImagePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection,true);
                    }
                    else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                        Log.i("type","com.android.providers.downloads.documents");
                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(docId));
                        imagePath = getImagePath(contentUri, null,true);
                    }
                    else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        Log.i("type","file");
                        imagePath = uri.getPath();
                    }
                }
                else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    Log.i("type","content");
                    imagePath = getImagePath(uri, null,true);
                }
                File nowAvatar = new File(imagePath);
                HttpRequestManager http = HttpRequestManager.getInstance(requireContext());
                MyCallBack callback = new MyCallBack(1);
                HashMap<String, Object> photoData = new HashMap<>();
                photoData.put("file", nowAvatar);
                photoData.put("userid", UserInfo.getInstance().getUserid());
                photoData.put("type", Integer.toString(3));
                http.upLoadFile("api/file/upload", photoData, callback);
            }
        }
        else if (requestCode == 0x000103) {
            if (resultCode==Activity.RESULT_OK){
                // 使用try让程序运行在内报错
                try {
                    //将图片保存
                    Bitmap bitmap= BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));

                    String TargetPath = FileManager.getInstance()
                            .getUserFileAbsolutePath(requireContext(), "image.jpg");
                    File saveFile = new File(TargetPath);
                    try {
                        FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                        // compress - 压缩的意思
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                        //存储完成后需要清除相关的进程
                        saveImgOut.flush();
                        saveImgOut.close();
                        Log.d("Save Bitmap", "The picture is save to your phone!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    File nowAvatar = new File(TargetPath);
                    HttpRequestManager http = HttpRequestManager.getInstance(requireContext());
                    MyCallBack callback = new MyCallBack(1);
                    HashMap<String, Object> photoData = new HashMap<>();
                    photoData.put("file", nowAvatar);
                    photoData.put("userid", UserInfo.getInstance().getUserid());
                    photoData.put("type", Integer.toString(1));
                    http.upLoadFile("api/file/upload", photoData, callback);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == 0x000104) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    Log.i("Author",uri.getAuthority());
                    String imagePath = "";
                    if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            //Log.d(TAG, uri.toString());
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            //Log.d(TAG, uri.toString());
                            Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            //如果是file类型的Uri，直接获取图片路径即可
                            imagePath = uri.getPath();
                        }
                    } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                        //Log.d(TAG, "content: " + uri.toString());
                        imagePath = getImagePath(uri, null);
                    }

                    File nowAvatar = new File(imagePath);
                    HttpRequestManager http = HttpRequestManager.getInstance(requireContext());
                    MyCallBack callback = new MyCallBack(1);
                    HashMap<String, Object> photoData = new HashMap<>();
                    photoData.put("file", nowAvatar);
                    photoData.put("userid", UserInfo.getInstance().getUserid());
                    photoData.put("type", Integer.toString(3));
                    http.upLoadFile("api/file/upload", photoData, callback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String getImagePath(Uri uri, String selection, boolean isVideo) {
        String path = null;
        Cursor cursor = requireContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index;
                if(!isVideo)
                index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                else
                    index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
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
            if(!FileManager.getInstance().isFileExist(FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draft/")))
                FileManager.getInstance().createDirection(FileManager.getInstance()
                        .getUserFileAbsolutePath(requireContext(), "Draft/"));
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
            newdata.put(DRAFT_LOCATION, mlocation);
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
    void DiscardAllChanges(){
        titleEdit.setText("");
        contentEdit.setText("");
        postType = 0;
        mFileid = -1;
        mlocation = "";
        ClearFileInfo();
        Refresh();
        if(draftName != null) {
            String filepath;
            filepath = FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draft/" + draftName);
            FileManager.getInstance().deleteFile(filepath);
        }
    }
}