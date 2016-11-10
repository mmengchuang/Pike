package com.xdlteam.pike.camera;

import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.contract.IFragCameraContract;
import com.xdlteam.pike.util.NetworkUtils;
import com.xdlteam.pike.widget.RecoderProgress;
import com.yixia.camera.MediaRecorder;
import com.yixia.camera.MediaRecorderFilter;
import com.yixia.camera.VCamera;
import com.yixia.camera.model.MediaObject;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 11655 on 2016/11/3.
 */

public class FragCameraPresenterImpl2 implements IFragCameraContract.IFragCameraPresenter,MediaRecorder.OnPreparedListener,MediaRecorder.OnErrorListener {

    private IFragCameraContract.IFragCameraView mFragView;
    // 显示视频的控件
    private SurfaceView mSurfaceView;
    private boolean mStartedFlg = false;
    // 录制视频的类
    private String TAG = "myTag";
    // 摄像头的状态,0表示后置，1表示前置
    private int cameraPosition = 1;
    //进度条
    private RecoderProgress mProgressBar;
    //保存录制视频的路径
    private static String mFilePath = "";
    //
    private MediaRecorder mMediaRecorder;
    private MediaObject mMediaObject;

    public FragCameraPresenterImpl2(IFragCameraContract.IFragCameraView mFragView) {
        this.mFragView = mFragView;
    }

    @Override
    public void initData() {
        //获取控件
        mSurfaceView = mFragView.getmActCameraSv();
        mProgressBar = mFragView.getmActCameraPb();
    }

    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderFilter();
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnPreparedListener(this);
        //WIFI下800k码率，其他情况（4G/3G/2G）600K码率
        mMediaRecorder.setVideoBitRate(NetworkUtils.isWifiAvailable(MyApplcation.context) ? MediaRecorder.VIDEO_BITRATE_MEDIUM : MediaRecorder.VIDEO_BITRATE_NORMAL);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);
        if (mMediaObject != null) {
            mMediaRecorder.prepare();
          /*  mMediaRecorder.setCameraFilter(MediaRecorderFilter.CAMERA_FILTER_NO);

            mProgressBar.setData(mMediaObject);*/
        } else {
            Toast.makeText(MyApplcation.context, "相机初始化失败", Toast.LENGTH_SHORT).show();
            mFragView.jumpActivity();
        }
    }
    /**
     * 相机的准备工作
     */
    //初始化Camera设置
    public void initCamera(int position) {
    }

    /**
     * 获取系统时间，保存文件以系统时间戳命名
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        int minute = ca.get(Calendar.MINUTE);
        int hour = ca.get(Calendar.HOUR);
        int second = ca.get(Calendar.SECOND);

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d("myTag", "date:" + date);

        return date;
    }

    /**
     * 获取SD path
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }

        return null;
    }

    /**
     * 获取录制视频的路径
     *
     * @return
     */
    public String getmFilePath() {
        return mFilePath;
    }

    @Override
    public void start() {//开始录制

    }

    @Override
    public void stop() {//停止录制
    }

    @Override
    public void changeCameraState() {
    }

    /**
     * 释放mCamera
     */
    @Override
    public void releaseCamera() {
    }

    @Override
    public void openLight() {

    }

    @Override
    public void closeLight() {

    }

    @Override
    public void delFile(String fileUrl) {
        File videoFile = new File(fileUrl);
        if (videoFile.exists()) {//文件存在,则删除文件
            videoFile.delete();
            mFragView.showMsg("视频文件删除成功!");
        } else {
            mFragView.showMsg("已经没有文件了,不要点击了哦!");
        }
    }

    @Override
    public void onVideoError(int i, int i1) {

    }

    @Override
    public void onAudioError(int i, String s) {

    }

    @Override
    public void onPrepared() {

    }
}
