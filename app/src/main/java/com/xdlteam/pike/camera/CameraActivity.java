package com.xdlteam.pike.camera;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;
import com.xdlteam.pike.contract.ICameraContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 11655 on 2016/11/3.
 */

public class CameraActivity extends BaseActivity implements ICameraContract.ICameraView {

    //获取控件
    @BindView(R.id.act_camera_vp)
    ViewPager mActCameraVp;
    @BindView(R.id.act_camera_btn_left)
    Button mActCameraBtnLeft;
    @BindView(R.id.act_camera_btn_right)
    Button mActCameraBtnRight;
    //定义控制层对象
    private CameraPresenterImpl mPresenter;
    //定义FragmentManger对象
    private FragmentManager mFragmentManager;
    //存放按钮，用于改变按钮的颜色
    private Button[] mBtns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        immersiveNotification();
        mPresenter = new CameraPresenterImpl(this);
        mBtns = new Button[]{mActCameraBtnRight, mActCameraBtnLeft};
        //初始化数据
        mPresenter.initData();
    }

    @Override
    protected void unBind() {

    }

    @Override
    public void showMsg(String msg) {

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
    public ViewPager getmActCameraVp() {
        return mActCameraVp;
    }

    @Override
    public FragmentManager getmFragmentManager() {
        return mFragmentManager = getSupportFragmentManager();
    }

    /**
     * 沉浸式通知栏
     */
    protected void immersiveNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @OnClick({R.id.act_camera_btn_left, R.id.act_camera_btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_camera_btn_left:
                //设置当前选中的viewpager
                mActCameraVp.setCurrentItem(0);
                changeBtnState(0);
                break;
            case R.id.act_camera_btn_right:
                mActCameraVp.setCurrentItem(1);
                changeBtnState(1);
                break;
        }
    }

    /**
     * 改变字体的颜色
     *
     * @param position 选中的位置
     */
    @Override
    public void changeBtnState(int position) {
        switch (position) {
            case 0:
                //改变字体颜色
                mActCameraBtnLeft.setTextColor(Color.parseColor("#ea9518"));
                mActCameraBtnRight.setTextColor(Color.parseColor("#ABAEAB"));
                break;
            case 1:
                mActCameraBtnLeft.setTextColor(Color.parseColor("#ABAEAB"));
                mActCameraBtnRight.setTextColor(Color.parseColor("#ea9518"));
                break;
        }
    }
}
