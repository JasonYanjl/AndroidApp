package com.example.frontend;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.example.frontend.placeholder.PlaceholderContent;
import com.example.frontend.utils.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class DraftFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    @BindView(R.id.draft_list)
    protected RecyclerView recyclerView;
    private List<String> drafts;
    private List<String> title;
    private List<String> text;
    final static String DRAFT_TITLE = "DRAFT_TITLE_POST_EDITOR";
    final static String DRAFT_CONTENT = "DRAFT_CONTENT_POST_EDITOR";
    final static String DRAFT_FILEID = "DRAFT_FILEID_POST_EDITOR";
    final static String DRAFT_POSTTYPE = "DRAFT_POSTTYPE_POST_EDITOR";
    final static String DRAFT_LOCATION = "DRAFT_LOCATION";

    public void myItemClick(View view){
        // 获取itemView的位置
        int position = recyclerView.getChildAdapterPosition(view);
        PostFragment post= (PostFragment) getParentFragment();
        post.editDraft(drafts.get(position));
    }


    public class TestRecycleViewAdapter extends RecyclerView.Adapter<TestRecycleViewAdapter.ViewHolderA> {
        private Context mContext;
        private List<String> mList, mTitle, mText;

        public TestRecycleViewAdapter(Context context, List<String> list,  List<String> title,
                                      List<String> text) {
            mContext = context;
            mList = list;
            mTitle = title;
            mText = text;
        }

        @Override
        public ViewHolderA onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.draft_item, parent, false);
            ViewHolderA holderA = new ViewHolderA(view);
            return holderA;
        }

        @Override
        public void onBindViewHolder(ViewHolderA holder, int position) {
            holder.mTextView.setText(mList.get(position)+"的草稿"+"\n"+
                    "标题："+mTitle.get(position) + "\n" +
                    "内容："+mText.get(position));
        }

        @Override
        public int getItemCount() {
            return mList==null?0:mList.size();
        }

        class ViewHolderA extends RecyclerView.ViewHolder{

            TextView mTextView;
            public ViewHolderA(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.recycle_textview);
                mTextView.setOnClickListener(view -> myItemClick(view));
            }
        }
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DraftFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DraftFragment newInstance() {
        return  new DraftFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draft_list, container, false);

        ButterKnife.bind(this, view);
        // Set the adapter
        initData();
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new TestRecycleViewAdapter(requireContext(),drafts,title,text));
        }
        return view;
    }
    public List<String> readTitleText(String filepath) {
        List<String> res = new ArrayList<>();
        FileInputStream inputStream = null;
        try {
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
                String titleStr = newdata.getString(DRAFT_TITLE);
                String contentStr = newdata.getString(DRAFT_CONTENT);
                res.add(titleStr);
                res.add(contentStr);
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
        return res;
    }
    void initData() {
        drafts = new ArrayList<>();
        title = new ArrayList<>();
        text = new ArrayList<>();

        String filepath = FileManager.getInstance()
                .getUserFileAbsolutePath(requireContext(), "Draft/");
        drafts.add("自动保存");

        String autofilepath = FileManager.getInstance()
                .getUserFileAbsolutePath(requireContext(), "Draftcache");
        List<String> tmpres = readTitleText(autofilepath);
        if (tmpres.size() >= 2) {
            title.add(tmpres.get(0));
            text.add(tmpres.get(1));
        } else {
            title.add("");
            text.add("");
        }

        for (String filename : getAllFiles(filepath)) {
            drafts.add(filename);
            autofilepath = FileManager.getInstance()
                    .getUserFileAbsolutePath(requireContext(), "Draft/"+filename);
            tmpres = readTitleText(autofilepath);
            if (tmpres.size() >= 2) {
                title.add(tmpres.get(0));
                text.add(tmpres.get(1));
            } else {
                title.add("");
                text.add("");
            }
        }
    }

    public static List<String> getAllFiles(String dirPath) {

        File f = new File(dirPath);
        if (!f.exists()) {
            return new ArrayList<>();
        }

        File[] files = f.listFiles();

        if(files==null){
            return new ArrayList<>();
        }
        List<String> fileList = new ArrayList<>();
        for (File _file : files) {
            if(_file.isFile()){
                String fileName = _file.getName();
                try {
                    fileList.add(fileName);
                }catch (Exception e){
                }
            }
        }
        return fileList;
    }
}