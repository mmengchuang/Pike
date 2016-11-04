package com.xdlteam.pike.camera;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xdlteam.pike.R;
import com.xdlteam.pike.contract.IFragCameraContract;
import com.xdlteam.pike.release.ReleaseActivity;
import com.xdlteam.pike.widget.RecoderProgress;

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
    @BindView(R.id.frag_camera_recodrProgress)
    RecoderProgress mActCameraPb;
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
    /**
     * 用于标记进度条的状态
     * 默认为false 为未开启状态, true 为开始前状态
     */
    private boolean isStart = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://时间结束之后,结束录制
                    if (flag){//如果未点击按钮,正在录制
                        //结束录制
                        mPresenter.stop();
                        //将按钮状态设置成默认为点击状态
                        mActCameraIvStart.setImageResource(R.drawable.act_camera_start);
                        mActCameraPb.stopAnimation();
                        flag = !flag;
                        isStart = false;
                        //显示按钮
                        showBtns();
                    }
                    break;
            }
        }
    };
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
        //设置按钮为不可显示状态
        hideBtns();
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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

    @Override
    public RecoderProgress getmActCameraPb() {
        return mActCameraPb;
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
                mPresenter.delFile(mPresenter.getmFilePath());
                mActCameraIvOk.setVisibility(View.INVISIBLE);
                break;
            case R.id.act_camera_iv_start:
                if (!flag) {//如果是第一次点击
                    mPresenter.start();
                    mActCameraIvStart.setImageResource(R.drawable.act_camera_pause);
                    //隐藏按钮
                    hideBtns();
                    if(!isStart){//判断进度条是否为启动状态
                        mActCameraPb.startAnimation();//启动动画
                        /**
                         * 开启一个倒计时,用于动画结束之后，重置按钮
                         */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(10000);//使线程休眠10s
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message message = Message.obtain();
                                message.what = 0;
                                mHandler.sendMessage(message);
                            }
                        }).start();
                    }else{
                        mActCameraPb.stopAnimation();
                    }
                    isStart = !isStart;
                } else {//第二次点击停止录制
                    mPresenter.stop();
                    mActCameraIvStart.setImageResource(R.drawable.act_camera_start);
                    mActCameraPb.stopAnimation();
                    isStart = false;
                    //显示按钮
                    showBtns();
                }
                flag = !flag;
                break;
            case R.id.act_camera_iv_ok://点击对勾按钮
                Intent intent = new Intent(getContext(), ReleaseActivity.class);//跳转到发布视频页面
                intent.putExtra("ViedeoLocalURL",mPresenter.getmFilePath());//将小视频路径传递到下一个页面
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示按钮的方法
     */
    void showBtns(){
        mActCameraIvDel.setVisibility(View.VISIBLE);
        mActCameraIvOk.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏按钮
     */
    void hideBtns(){
        mActCameraIvDel.setVisibility(View.INVISIBLE);
        mActCameraIvOk.setVisibility(View.INVISIBLE);
    }
}
