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
         * 获取显示视频的控件对象
         * @return surfaceView 控件对象
         */
        SurfaceView getmActCameraSv();

        /**
         * 获取显示进度条控件对象
         * @return 进度条控件对象
         */
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

        /**
         * 从本地删除刚刚录制的视频文件
         * @param fileUrl 文件路径
         */
        void delFile(String fileUrl);
    }
}
