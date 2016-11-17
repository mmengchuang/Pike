package com.xdlteam.pike.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xdlteam.pike.contract.IFragCameraContract;
import com.xdlteam.pike.util.LogUtils;
import com.xdlteam.pike.widget.RecoderProgress;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.yixia.weibo.sdk.MediaRecorderBase.MAX_FRAME_RATE;
import static com.yixia.weibo.sdk.MediaRecorderBase.MIN_FRAME_RATE;

/**
 * Created by 11655 on 2016/11/3.
 */

public class FragCameraPresenterImpl implements IFragCameraContract.IFragCameraPresenter, SurfaceHolder.Callback, Camera.PreviewCallback {

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
    private SurfaceHolder holder;
    // 摄像头的状态,0表示后置，1表示前置
    private int cameraPosition = 1;
    //进度条
    private RecoderProgress mProgressBar;
    //保存录制视频的路径
    private static String mFilePath = "";
    //相机支持的尺寸
    private List<Camera.Size> mSupportedPreviewSizes;
    /**
     * 帧率
     */
    protected int mFrameRate = MIN_FRAME_RATE;
    private boolean mStartPreview;
    private boolean mPrepared;
    private int mCameraId = 0;
    private boolean mSurfaceCreated;

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
        setSurfaceHolder(holder);
        prepare();

    }

    /**
     * 设置预览输出SurfaceHolder
     *
     * @param sh
     */
    @SuppressWarnings("deprecation")
    public void setSurfaceHolder(SurfaceHolder sh) {
        if (sh != null) {
            sh.addCallback(this);
            if (!DeviceUtils.hasHoneycomb()) {
                sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        try {
            if (mStartPreview || mSurfaceHolder == null || !mPrepared) {
                return;
            } else {
                mStartPreview = true;
            }

            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                myCamera = Camera.open();
            } else {
                myCamera = Camera.open(mCameraId);
                Log.i(TAG, "camera.open");
            }
            //设置相机旋转90度
            myCamera.setDisplayOrientation(90);
            myCamera.setPreviewDisplay(mSurfaceHolder);

            //设置摄像头参数
            myParameters = myCamera.getParameters();
            mSupportedPreviewSizes = myParameters.getSupportedPreviewSizes();//	获取支持的尺寸
            prepareCameraParaments();
            myCamera.setParameters(myParameters);
            //设置其他
            setPreviewCallback();
            myCamera.startPreview();
        } catch (Exception e) {
            mFragView.showMsg("相机初始化错误,请检查是否有其他APP占用相机");
            LogUtils.e("myTag", "相机初始化错误" + e.getLocalizedMessage());
        }

    }

    /**
     * 停止预览
     */

    public void stopPreview() {
        if (myCamera != null) {
            try {
                myCamera.stopPreview();
                myCamera.setPreviewCallback(null);
                // camera.lock();
                myCamera.release();
            } catch (Exception e) {
                Log.e("myTag", "stopPreview...");
            }
            myCamera = null;
        }
        mStartPreview = false;
    }

    /**
     * 预处理一些拍摄参数
     * 注意：自动对焦参数cam_mode和cam-mode可能有些设备不支持，导致视频画面变形，需要判断一下，已知有"GT-N7100", "GT-I9308"会存在这个问题
     */
    @SuppressWarnings("deprecation")
    protected void prepareCameraParaments() {
        if (myParameters == null)
            return;

        List<Integer> rates = myParameters.getSupportedPreviewFrameRates();
        if (rates != null) {
            if (rates.contains(MAX_FRAME_RATE)) {
                mFrameRate = MAX_FRAME_RATE;
            } else {
                Collections.sort(rates);
                for (int i = rates.size() - 1; i >= 0; i--) {
                    if (rates.get(i) <= MAX_FRAME_RATE) {
                        mFrameRate = rates.get(i);
                        break;
                    }
                }
            }
        }

        myParameters.setPreviewFrameRate(mFrameRate);
        // mParameters.setPreviewFpsRange(15 * 1000, 20 * 1000);
        myParameters.setPreviewSize(640, 480);// 3:2

        // 设置输出视频流尺寸，采样率
        myParameters.setPreviewFormat(ImageFormat.NV21);

        //设置自动连续对焦
        String mode = getAutoFocusMode();
        if (StringUtils.isNotEmpty(mode)) {
            myParameters.setFocusMode(mode);
        }

        //设置人像模式，用来拍摄人物相片，如证件照。数码相机会把光圈调到最大，做出浅景深的效果。而有些相机还会使用能够表现更强肤色效果的色调、对比度或柔化效果进行拍摄，以突出人像主体。
        //		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && isSupported(mParameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_PORTRAIT))
        //			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

        if (isSupported(myParameters.getSupportedWhiteBalance(), "auto"))
            myParameters.setWhiteBalance("auto");

        //是否支持视频防抖
        if ("true".equals(myParameters.get("video-stabilization-supported")))
            myParameters.set("video-stabilization", "true");

        //		mParameters.set("recording-hint", "false");
        //
        //		mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (!DeviceUtils.isDevice("GT-N7100", "GT-I9308", "GT-I9300")) {
            myParameters.set("cam_mode", 1);
            myParameters.set("cam-mode", 1);
        }
    }

    /**
     * 连续自动对焦
     */
    private String getAutoFocusMode() {
        if (myParameters != null) {
            //持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
            List<String> focusModes = myParameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    /**
     * 检测是否支持指定特性
     */
    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
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
     * 开始预览
     */
    public void prepare() {
        mPrepared = true;
        if (mSurfaceCreated)
            startPreview();
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
        stopPreview();
    }

    /**
     * 设置回调
     */
    protected void setPreviewCallback() {
        myCamera.setPreviewCallback(this);
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
        this.mSurfaceHolder = holder;
        this.mSurfaceCreated = true;
        if (mPrepared && !mStartPreview) {
            startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // surfaceDestroyed的时候同时对象设置为null
        this.mSurfaceHolder = null;
        this.mSurfaceCreated = false;
        this.mSurfaceView = null;
        if (this.mRecorder != null) {
            this.mRecorder.release();
            this.mRecorder = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //底层实时处理视频，将视频旋转好，并剪切成480x480
        byte [] newBytes  = new byte[bytes.length];
        YV12RotateNegative90(newBytes,bytes,camera.getParameters().getPreviewSize().width,camera.getParameters().getPreviewSize().height);
        LogUtils.i("myTag","视频的数组"+newBytes.toString());
    }
    /**
     * 旋转数据
     *
     * @param dst
     *            目标数据
     * @param src
     *            源数据
     * @param srcWidth
     *            源数据宽
     * @param srcHeight
     *            源数据高
     */
    private void YV12RotateNegative90(byte[] dst, byte[] src, int srcWidth,
                                      int srcHeight) {
        int t = 0;
        int i, j;

        int wh = srcWidth * srcHeight;

        for (i = srcWidth - 1; i >= 0; i--) {
            for (j = srcHeight - 1; j >= 0; j--) {
                dst[t++] = src[j * srcWidth + i];
            }
        }

        for (i = srcWidth / 2 - 1; i >= 0; i--) {
            for (j = srcHeight / 2 - 1; j >= 0; j--) {
                dst[t++] = src[wh + j * srcWidth / 2 + i];
            }
        }

        for (i = srcWidth / 2 - 1; i >= 0; i--) {
            for (j = srcHeight / 2 - 1; j >= 0; j--) {
                dst[t++] = src[wh * 5 / 4 + j * srcWidth / 2 + i];
            }
        }

    }
}
