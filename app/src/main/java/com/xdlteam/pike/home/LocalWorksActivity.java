package com.xdlteam.pike.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xdlteam.pike.R;
import com.xdlteam.pike.config.Contracts;
import com.xdlteam.pike.util.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 本地作品集
 */
public class LocalWorksActivity extends AppCompatActivity {

    @BindView(R.id.act_local_works_iv_back)
    ImageButton mActLocalWorksIvBack;
    @BindView(R.id.act_local_works_tv_choose)
    TextView mActLocalWorksTvChoose;
    @BindView(R.id.act_local_works_gv)
    GridView mActLocalWorksGv;
    @BindView(R.id.act_local_works_btn_del)
    Button mActLocalWorksBtnDel;
    @BindView(R.id.act_local_works_btn_canel)
    Button mActLocalWorksBtnCanel;

    private static final String TAG = LocalWorksActivity.class.getSimpleName();
    private static List<String> fileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_works);
        ButterKnife.bind(this);
        initDatas();
    }

    /*初始化数据*/
    private void initDatas() {
        fileNames = new ArrayList<>();
        //循环遍历sdcard目录,列出所有视频
        try {
            if (readfile(Contracts.DEFAULT_VIDEO_CACHE)){//文件遍历完毕
                LogUtils.i(TAG,"文件遍历完毕!");
                //设置适配器
                mActLocalWorksGv.setAdapter(new GridViewAdapter(this,fileNames));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.act_local_works_iv_back, R.id.act_local_works_tv_choose, R.id.act_local_works_btn_del, R.id.act_local_works_btn_canel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_local_works_iv_back:
                break;
            case R.id.act_local_works_tv_choose:
                break;
            case R.id.act_local_works_btn_del:
                break;
            case R.id.act_local_works_btn_canel:
                break;
        }
    }

    /**
     * 读取某个文件夹下的所有文件
     */
    public static boolean readfile(String filepath) throws FileNotFoundException, IOException {
        try {

            File file = new File(filepath);
            if (!file.isDirectory()) {//判断是否为文件夹
                LogUtils.i(TAG, "文件");
                LogUtils.i(TAG, "path=" + file.getPath());
                LogUtils.i(TAG, "absolutepath=" + file.getAbsolutePath());
                LogUtils.i(TAG, "name=" + file.getName());

            } else if (file.isDirectory()) {
                LogUtils.i(TAG, "文件夹");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + File.separator + filelist[i]);
                    if (!readfile.isDirectory()) {
                        LogUtils.i(TAG, "path=" + readfile.getPath());
                        LogUtils.i(TAG, "absolutepath="
                                + readfile.getAbsolutePath());
                        LogUtils.i(TAG, "name=" + readfile.getName());
                        //将文件名称保存到集合中
                        fileNames.add(readfile.getPath());

                    } else if (readfile.isDirectory()) {
                        readfile(filepath + File.separator + filelist[i]);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            LogUtils.e(TAG, "readfile()   Exception:" + e.getMessage());
        }
        return true;
    }

    /**
     * 删除某个文件夹下的所有文件夹和文件
     */


    public static boolean deletefile(String delpath)
            throws FileNotFoundException, IOException {
        try {

            File file = new File(delpath);
            if (!file.isDirectory()) {
                LogUtils.i(TAG,"1");
                file.delete();
            } else if (file.isDirectory()) {
                LogUtils.i(TAG,"2");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        LogUtils.i(TAG,"path=" + delfile.getPath());
                        LogUtils.i(TAG,"absolutepath="
                                + delfile.getAbsolutePath());
                        LogUtils.i(TAG,"name=" + delfile.getName());
                        delfile.delete();
                        LogUtils.i(TAG,"删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + File.separator + filelist[i]);
                    }
                }
                file.delete();

            }

        } catch (FileNotFoundException e) {
            LogUtils.e(TAG,"deletefile()   Exception:" + e.getMessage());
        }
        return true;
    }

}
