package com.xdlteam.pike.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xdlteam.pike.contract.IFragCameraContract;
import com.xdlteam.pike.util.LogUtils;
import com.xdlteam.pike.widget.RecoderProgress;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by 11655 on 2016/11/3.
 */

public class FragCameraPresenterImpl implements IFragCameraContract.IFragCameraPresenter, SurfaceHolder.Callback {

    private IFragCameraContract.IFragCameraView mFragView;
    // 显示视频的控件
    private SurfaceView mSurfaceView;
    private boolean mStartedFlg = false;
    // 录制视频的类
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看
    // 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private String TAG = "myTag";
    private Camera myCamera;
    private Camera.Parameters myParameters;
    private Camera.AutoFocusCallback mAutoFocusCallback = null;
    private boolean isView = false;
    private SurfaceHolder holder;
    // 摄像头的状态,0表示后置，1表示前置
    private int cameraPosition = 1;
    //进度条
    private RecoderProgress mProgressBar;
    //保存录制视频的路径
    private static String mFilePath = "";

    public FragCameraPresenterImpl(IFragCameraContract.IFragCameraView mFragView) {
        this.mFragView = mFragView;
    }

    @Override
    public void initData() {
        //获取控件
        mSurfaceView = mFragView.getmActCameraSv();
        mProgressBar = mFragView.getmActCameraPb();
        //重写AutoFocusCallback接口
        mAutoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Log.i(TAG, "AutoFocus: success...");
                } else {
                    Log.i(TAG, "AutoFocus: failure...");
                }
            }
        };
        holder = mSurfaceView.getHolder();// 取得holder

        holder.addCallback(this); // holder加入回调接口
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 相机的准备工作
     */
    //初始化Camera设置
    public void initCamera(int position) {
        if (myCamera == null && !isView) {
            //默认打开后置摄像头
            myCamera = Camera.open(position);
            Log.i(TAG, "camera.open");
        }
        if (myCamera != null && !isView) {
            try {
                myParameters = myCamera.getParameters();
                myParameters.setPreviewSize(1920, 1080);
                myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                myCamera.setParameters(myParameters);
                //设置相机旋转90度
                myCamera.setDisplayOrientation(90);
                myCamera.setPreviewDisplay(mSurfaceHolder);
                myCamera.startPreview();
                isView = true;
            } catch (Exception e) {
                e.printStackTrace();
                mFragView.showMsg("相机初始化错误");
                LogUtils.e("myTag", "相机初始化错误" + e.getLocalizedMessage());
            }
        }
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
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
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
        if (!mStartedFlg) {
            // Start
            if (mRecorder == null) {
                mRecorder = new MediaRecorder(); // Create MediaRecorder
            }
            try {
                myCamera.unlock();
                mRecorder.setCamera(myCamera);
                // Set audio and video source and encoder
                // 这两项需要放在setOutputFormat之前
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                //解决录制视频之后旋转90度问题
                mRecorder.setOrientationHint(90);
                mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                // Set output file path
                String path = getSDPath();
                if (path != null) {

                    File dir = new File(path + "/Pike");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    path = dir + "/" + getDate() + ".mp4";
                    /*2016.11.4 修改*/
                    //将小视频的路径地址保存到全局,方便在上传视频时获取视频路径
                    mFilePath = path;
                    mRecorder.setOutputFile(path);
                    Log.d(TAG, "bf mRecorder.prepare()");
                    mRecorder.prepare();
                    Log.d(TAG, "af mRecorder.prepare()");
                    Log.d(TAG, "bf mRecorder.start()");
                    mRecorder.start();   // Recording is now started
                    Log.d(TAG, "af mRecorder.start()");
                    mStartedFlg = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {//停止录制
        if (mStartedFlg) {
            try {
                mRecorder.stop();
                mRecorder.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mStartedFlg = false;
    }

    @Override
    public void changeCameraState() {
        // 切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
        LogUtils.i("myTag", "我获取到的摄像头的个数" + cameraCount);
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
            if (cameraPosition == 1) {
                // 现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    /**
                     * 记得释放camera，方便其他应用调用
                     */
                    releaseCamera();
                    // 打开当前选中的摄像头
                    myCamera = Camera.open(i);
                    try {
                        //设置相机旋转90度
                        myCamera.setDisplayOrientation(90);
                        myCamera.setPreviewDisplay(mSurfaceHolder);
                        myCamera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraPosition = 0;
                    break;
                }
            } else {
                // 现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    /**
                     * 记得释放camera，方便其他应用调用
                     */
                    releaseCamera();
                    myCamera = Camera.open(i);
                    try {
                        //设置相机旋转90度
                        myCamera.setDisplayOrientation(90);
                        myCamera.setPreviewDisplay(mSurfaceHolder);
                        myCamera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    /**
     * 释放mCamera
     */
    @Override
    public void releaseCamera() {
        if (myCamera != null) {
            myCamera.setPreviewCallback(null);
            myCamera.stopPreview();// 停掉原来摄像头的预览
            myCamera.release();
            myCamera = null;
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void openLight() {
        Camera.Parameters mParameters;
        mParameters = myCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        myCamera.setParameters(mParameters);
    }

    @Override
    public void closeLight() {
        Camera.Parameters mParameters;
        mParameters = myCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        myCamera.setParameters(mParameters);
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
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = holder;
        initCamera(0);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // surfaceDestroyed的时候同时对象设置为null
        mSurfaceView = null;
        mSurfaceHolder = null;
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
