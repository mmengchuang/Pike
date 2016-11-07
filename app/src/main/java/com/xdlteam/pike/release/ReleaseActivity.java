package com.xdlteam.pike.release;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;
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
    int progress = 0;
    //普通进度条对话框
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        ButterKnife.bind(this);
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
    public void showMsg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog(){
        if(dialog==null){
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle("");
        dialog.setMessage("加载中...");
        dialog.setCancelable(false);
        dialog.show();
    }
    /**
     * 功能 ：取消一个进度条对话框
     */
    @Override
    public void dismissProcessDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }


    @Override
    public void jumpActivity() {
        finish();
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
                finish();
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