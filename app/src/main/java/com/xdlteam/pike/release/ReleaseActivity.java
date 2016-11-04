package com.xdlteam.pike.release;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;
import com.xdlteam.pike.base.BasePresenter;
import com.xdlteam.pike.contract.IReleaseContract;
import com.xdlteam.pike.location.LocationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布视频的页面
 * Created by 11655 on 2016/11/4.
 */

public class ReleaseActivity extends BaseActivity implements IReleaseContract.IReleaseView {

    //获取控件
    @BindView(R.id.act_release_iv_exit)
    ImageView mActReleaseIvExit;
    @BindView(R.id.act_release_iv_ok)
    ImageView mActReleaseIvOk;
    @BindView(R.id.act_release_iv_first_video)
    ImageView mActReleaseIvFirstVideo;
    @BindView(R.id.act_release_et_content)
    EditText mActReleaseEtContent;
    @BindView(R.id.act_release_btn_location)
    Button mActReleaseBtnLocation;
    @BindView(R.id.act_release_gv)
    GridView mActReleaseGv;
    private IReleaseContract.IReleasePresenter mPresenter;
    private SVProgressHUD mSVProgressHUD;
    int progress = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            progress = progress + 5;
            if (mSVProgressHUD.getProgressBar().getMax() != mSVProgressHUD.getProgressBar().getProgress()) {
                mSVProgressHUD.getProgressBar().setProgress(progress);
                mSVProgressHUD.setText("进度 "+progress+"%");

                mHandler.sendEmptyMessageDelayed(0,500);
            }
            else{
                mSVProgressHUD.dismiss();
            }

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        ButterKnife.bind(this);
        //初始化进度条
        mSVProgressHUD = new SVProgressHUD(this);
        mPresenter = new ReleasePresenterImpl(this);
        mPresenter.initData();
        mPresenter.setDatas(getDatas());
    }

    /**
     * 获取从上一个Activity 传递过来的视频路径信息
     */
    private String getDatas() {
        Intent intent = getIntent();
        String fileUrl = intent.getStringExtra("ViedeoLocalURL");
        return fileUrl;
    }

    @Override
    protected void unBind() {

    }

    @Override
    public void setPresenter(BasePresenter presenter) {

    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog(){
        mSVProgressHUD.show();
    }
    @Override
    public void showLoadingDialog(String title, String msg, boolean flag) {
        progress = 0;
        mSVProgressHUD.getProgressBar().setProgress(progress);//先重设了进度再显示，避免下次再show会先显示上一次的进度位置所以要先将进度归0
        mSVProgressHUD.showWithProgress("进度 " + progress + "%", SVProgressHUD.SVProgressHUDMaskType.Black);
//        mHandler.sendEmptyMessageDelayed(0,500);
        progress = Integer.parseInt(msg);
        mHandler.sendMessage(new Message());
    }

    @Override
    public void canelLoadingDialog() {
        mSVProgressHUD.dismiss();
    }

    @Override
    public void jumpActivity() {

    }
    @Override
    public ImageView getmActReleaseIvFirstVideo() {
        return mActReleaseIvFirstVideo;
    }
    @Override
    public GridView getmActReleaseGv() {
        return mActReleaseGv;
    }

    @Override
    public EditText getmActReleaseEtContent() {
        return mActReleaseEtContent;
    }
    @Override
    public Button getmActReleaseBtnLocation() {
        return mActReleaseBtnLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==500){
            //设置地理位置为用户选中的信息
            mActReleaseBtnLocation.setText(data.getStringExtra("locationMsg"));
        }
    }

    @OnClick({R.id.act_release_iv_exit, R.id.act_release_iv_ok, R.id.act_release_btn_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_release_iv_exit://退出发布
                break;
            case R.id.act_release_iv_ok://点击发布
                mPresenter.release();
                break;
            case R.id.act_release_btn_location://点击获取位置信息
                startActivityForResult(new Intent(this, LocationActivity.class),500);
                break;
        }
    }
}
