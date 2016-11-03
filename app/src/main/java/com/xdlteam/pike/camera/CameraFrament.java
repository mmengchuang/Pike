package com.xdlteam.pike.camera;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xdlteam.pike.R;
import com.xdlteam.pike.contract.IFragCameraContract;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 11655 on 2016/11/3.
 */

public class CameraFrament extends BaseFragment implements IFragCameraContract.IFragCameraView {

    //获取控件
    @BindView(R.id.act_camera_sv)
    SurfaceView mActCameraSv;
    @BindView(R.id.act_camera_iv_exit)
    ImageView mActCameraIvExit;
    @BindView(R.id.act_camera_iv_light)
    ImageView mActCameraIvLight;
    @BindView(R.id.act_camera_iv_change)
    ImageView mActCameraIvChange;
    @BindView(R.id.act_camera_iv_music)
    ImageView mActCameraIvMusic;
    @BindView(R.id.act_camera_pb)
    ProgressBar mActCameraPb;
    @BindView(R.id.act_camera_iv_del)
    ImageView mActCameraIvDel;
    @BindView(R.id.act_camera_iv_start)
    ImageView mActCameraIvStart;
    @BindView(R.id.act_camera_iv_ok)
    ImageView mActCameraIvOk;
    private FragCameraPresenterImpl mPresenter;
    /**
     * 用于标记按钮的当前状态
     * 默认为false 没有开始录制，true 为正在录制
     */
    private boolean flag = false;
    /**
     * 用于标记闪光灯的状态
     * 默认为 false 未开启状态 ,true 为开启状态
     */
    private boolean lightFlag = false;
    @Override
    protected void lazyLoad() {

    }

    @Override
    public View initLayout(LayoutInflater inflater, ViewGroup container, boolean b) {
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return inflater.inflate(R.layout.frag_camera, null);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter = new FragCameraPresenterImpl(this);
        mPresenter.initData();
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog(String title, String msg, boolean flag) {

    }

    @Override
    public void canelLoadingDialog() {

    }

    @Override
    public void jumpActivity() {

    }

    @Override
    public void onPause() {
        super.onPause();
        //释放相机资源
        mPresenter.releaseCamera();
    }

    @Override
    public SurfaceView getmActCameraSv() {
        return mActCameraSv;
    }

    @OnClick({R.id.act_camera_iv_exit,R.id.act_camera_iv_light,R.id.act_camera_iv_change, R.id.act_camera_iv_music, R.id.act_camera_iv_del, R.id.act_camera_iv_start, R.id.act_camera_iv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_camera_iv_exit:
                //退出当前的Activity
                getActivity().finish();
                break;
            case R.id.act_camera_iv_light:
                if (!lightFlag){//为关闭状态，点击开启
                    mActCameraIvLight.setImageResource(R.drawable.act_camera_light_on);
                    mPresenter.openLight();
                }else {
                    mActCameraIvLight.setImageResource(R.drawable.act_camera_light_off);
                    mPresenter.closeLight();
                }
                lightFlag = !lightFlag;
                break;
            case R.id.act_camera_iv_change:
                mPresenter.changeCameraState();
                break;
            case R.id.act_camera_iv_music:
                break;
            case R.id.act_camera_iv_del:
                break;
            case R.id.act_camera_iv_start:
                if (!flag) {//如果是第一次点击
                    mPresenter.start();
                    mActCameraIvStart.setImageResource(R.drawable.act_camera_pause);
                } else {
                    mPresenter.stop();
                    mActCameraIvStart.setImageResource(R.drawable.act_camera_start);
                }
                flag = !flag;
                break;
            case R.id.act_camera_iv_ok:
                break;
        }
    }
}
