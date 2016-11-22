package com.xdlteam.pike.camera;

import android.view.SurfaceView;

import com.xdlteam.pike.contract.IFragCameraContract;

/**
 * Created by 11655 on 2016/11/18.
 */

public class FragCameraPresenterImpl implements IFragCameraContract.IFragCameraPresenter {
    //视图层的View
    private IFragCameraContract.IFragCameraView mCameraView;
    // 显示视频的控件
    private SurfaceView mSurfaceView;
    //保存录制视频的路径
    private static String mFilePath = "";
    public FragCameraPresenterImpl(IFragCameraContract.IFragCameraView mCameraView) {
        this.mCameraView = mCameraView;
    }

    @Override
    public void initData() {
        //获取控件
        mSurfaceView = mCameraView.getmActCameraSv();

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void changeCameraState() {

    }

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

    }

    @Override
    public String getmFilePath() {
        return null;
    }
}
