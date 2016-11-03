package com.xdlteam.pike.camera;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.xdlteam.pike.contract.ICameraContract;

import java.util.ArrayList;
import java.util.List;

/**
 * 相机的操作类
 * @author mmengchen
 */

public class CameraPresenterImpl implements ICameraContract.ICameraPresenter {
    private ICameraContract.ICameraView mCameraView;
    //数据源
    private List<Fragment> mFragmentList;
    //控件
    private ViewPager mVp;
    //管理器
    private FragmentManager mFragmentManager;
    public CameraPresenterImpl(ICameraContract.ICameraView mCameraView) {
        this.mCameraView = mCameraView;
    }

    @Override
    public void initData() {
        //初始化数据源
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new PhotosFragment());
        mFragmentList.add(new CameraFrament());
        //获取控件和布局管理器
        mVp = mCameraView.getmActCameraVp();
        mFragmentManager = mCameraView.getmFragmentManager();
        //设置适配器
        mVp.setAdapter(new CameraAdapter(mFragmentManager,mFragmentList));
        //设置默认相机被选中
        mVp.setCurrentItem(1);
        mCameraView.changeBtnState(1);
        initEvent();
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //改变按钮颜色
                mCameraView.changeBtnState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
