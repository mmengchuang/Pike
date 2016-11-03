package com.xdlteam.pike.contract;

import android.view.SurfaceView;

import com.xdlteam.pike.widget.RecoderProgress;

/**
 * 契约类、使view 和 Presenter 之前的方法清晰
 * Created by 11655 on 2016/10/18.
 */

public class IFragCameraContract {
    public interface IFragCameraView {
        /**
         * /**
         * Toast数据
         *
         * @param msg
         */
        void showMsg(String msg);

        /**
         * 展示一个进度条对话框
         *
         * @param title 标题
         * @param msg   显示的内容
         * @param flag  是否可以取消
         */
        void showLoadingDialog(String title, String msg, boolean flag);

        /**
         * 取消进度条
         */
        void canelLoadingDialog();

        /**
         * activity的跳转
         */
        void jumpActivity();

        SurfaceView getmActCameraSv();

        RecoderProgress getmActCameraPb();
    }

    public interface IFragCameraPresenter {

        /**
         * 初始化数据
         */
        void initData();

        /**
         * 开始录制视频
         */
        void start();

        /**
         * 停止录制视频
         */
        void stop();

        /**
         * 前后摄像头的切换
         */
        void changeCameraState();

        /**
         * 释放相机资源
         */
        void releaseCamera();

        /**
         * 开启闪关灯
         */
        void openLight();

        /**
         * 关闭闪关灯
         */
        void closeLight();
    }
}
