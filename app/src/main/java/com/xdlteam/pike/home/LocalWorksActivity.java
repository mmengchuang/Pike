package com.xdlteam.pike.home;

import android.graphics.Bitmap;
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
import com.xdlteam.pike.util.VideoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private List<String> fileNames;
    private GridViewAdapter mAdapter;
    private List<Bitmap> bitmaps;

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
        bitmaps = new ArrayList<>();
        //设置默认下面按钮不显示
        switchBtn(View.GONE);
        //设置适配器
        mAdapter = new GridViewAdapter(this, bitmaps);
        mActLocalWorksGv.setAdapter(mAdapter);
        //循环遍历sdcard目录,列出所有视频
        try {
            readfile(Contracts.DEFAULT_VIDEO_CACHE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {
                            LogUtils.i(TAG, "加载完成!");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LogUtils.e(TAG, "readfile()   Exception:" + throwable.getMessage());
                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            //将图片保存到集合中
                            bitmaps.add(bitmap);
                            //更新数据源
                            mAdapter.notifyDataSetChanged();
                        }
                    });
            ;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.i(TAG,e.getLocalizedMessage());
        }
    }

    @OnClick({R.id.act_local_works_iv_back, R.id.act_local_works_tv_choose, R.id.act_local_works_btn_del, R.id.act_local_works_btn_canel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_local_works_iv_back:
                break;
            case R.id.act_local_works_tv_choose:
                //显示下面的按钮
                switchBtn(View.VISIBLE);
                break;
            case R.id.act_local_works_btn_del:
                break;
            case R.id.act_local_works_btn_canel:
                //隐藏按钮
                switchBtn(View.GONE);
                break;
        }
    }

    /**
     * 获取视频缓存目录下的所有视频的第一帧图片
     */
    private Observable<Bitmap> readfile(final String filepath) throws FileNotFoundException, IOException {
        //循环遍历sdcard目录,列出所有视频
        Observable<Bitmap> bitmapObservable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
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
                                //将视频文件的路径保存到集合中
                                fileNames.add(readfile.getPath());
                                //获取视频第一帧图片
                                Bitmap bitmap = VideoUtils.createVideoThumbnail(readfile.getPath());
                                subscriber.onNext(bitmap);
                            } else if (readfile.isDirectory()) {
                                readfile(filepath + File.separator + filelist[i]);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
        return bitmapObservable;
    }

    /**
     * 删除某个文件夹下的所有文件夹和文件
     */


    public static boolean deletefile(String delpath)
            throws FileNotFoundException, IOException {
        try {

            File file = new File(delpath);
            if (!file.isDirectory()) {
                LogUtils.i(TAG, "1");
                file.delete();
            } else if (file.isDirectory()) {
                LogUtils.i(TAG, "2");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        LogUtils.i(TAG, "path=" + delfile.getPath());
                        LogUtils.i(TAG, "absolutepath="
                                + delfile.getAbsolutePath());
                        LogUtils.i(TAG, "name=" + delfile.getName());
                        delfile.delete();
                        LogUtils.i(TAG, "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + File.separator + filelist[i]);
                    }
                }
                file.delete();

            }

        } catch (FileNotFoundException e) {
            LogUtils.e(TAG, "deletefile()   Exception:" + e.getMessage());
        }
        return true;
    }

    /**
     * 更改按钮的显示和隐藏状态
     * @param visibility
     */
    private void switchBtn(int visibility){
        mActLocalWorksBtnCanel.setVisibility(visibility);
        mActLocalWorksBtnDel.setVisibility(visibility);
    }
}
