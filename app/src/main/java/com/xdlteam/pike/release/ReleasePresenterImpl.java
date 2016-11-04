package com.xdlteam.pike.release;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.contract.IReleaseContract;
import com.xdlteam.pike.util.LogUtils;
import com.xdlteam.pike.util.VideoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 发布页面的处理类
 * Created by 11655 on 2016/11/4.
 */

public class ReleasePresenterImpl implements IReleaseContract.IReleasePresenter {
    //视图层对象
    private IReleaseContract.IReleaseView mReleaseView;
    //存放第一帧图片的控件对象
    private ImageView mFirstImageView;
    //用户的对视频的描述
    private EditText mEtDisc;
    //定位按钮
    private Button mBtn;
    //存放各种按钮的对象
    private GridView mGridView;
    //第一帧图片
    private Bitmap mBitmap;
    private String mVideoURL = "";

    public ReleasePresenterImpl(IReleaseContract.IReleaseView mReleaseView) {
        this.mReleaseView = mReleaseView;
    }

    @Override
    public void initData() {
        //获取控件信息
        mFirstImageView = mReleaseView.getmActReleaseIvFirstVideo();
        mGridView = mReleaseView.getmActReleaseGv();
        mEtDisc = mReleaseView.getmActReleaseEtContent();
        mBtn = mReleaseView.getmActReleaseBtnLocation();
    }

    @Override
    public void setDatas(String filePath) {
        //获取视频第一帧,并显示到图片控件上
        mBitmap = VideoUtils.createVideoThumbnail(filePath);
        mFirstImageView.setImageBitmap(mBitmap);
        //将url保存
        mVideoURL = filePath;

    }

    @Override
    public void release() {
        //显示进度条
        mReleaseView.showLoadingDialog();
        //获取第一帧图片的文件
        File imgFile = saveBitmapFile(mBitmap);
        //上传第一帧图片
        final BmobFile bmobImgFile = new BmobFile(imgFile);
        bmobImgFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                //取消进度条对话框
                mReleaseView.canelLoadingDialog();
                if (e == null) {//第一帧图片上传成功
                    LogUtils.i("myTag", "第一帧图片上传成功");
                    //上传视频
                    //显示进度条
                    mReleaseView.showLoadingDialog();
                    final BmobFile bmobVieoFile = new BmobFile(new File(mVideoURL));
                    bmobVieoFile.upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            //隐藏进度条对话框
                            mReleaseView.canelLoadingDialog();
                            if (e == null) {//上传成功,视频
                                //显示进度条
                               /*以下部分存在问题*/
                                SaveVideos(bmobImgFile, bmobVieoFile);

                            } else {
                                //取消进度条
                                mReleaseView.showMsg("分享视频失败!" + e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer value) {
                            super.onProgress(value);
                            //显示进度条
                            mReleaseView.showLoadingDialog("", value + "", false);
                        }
                    });
                } else {
                    mReleaseView.showMsg("分享视频失败!" + e.getLocalizedMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                //显示进度条
                mReleaseView.showLoadingDialog("", value + "", false);
            }
        });

    }

    /**
     * 保存视频到数据库
     * @param bmobImgFile 上传后的视频的第一帧图片
     * @param bmobVieoFile 上传后的视频文件
     */
    private void SaveVideos(BmobFile bmobImgFile, BmobFile bmobVieoFile) {
        //更新数据表
        Video video = new Video();
        //设置发布小视频的用户ID
        video.setUserId(BmobUser.getCurrentUser(User.class).getObjectId());
        //设置图片的缩略图
        video.setVideo_image(bmobImgFile);
        //设置视频发送的地点
        BDLocation bdLocation = (BDLocation) MyApplcation.getDatas("location",
                true);
        video.setVideo_point(new BmobGeoPoint(bdLocation.getLongitude(),
                bdLocation.getLatitude()));
        //设置视频文件
        video.setVideo_content(bmobVieoFile);
        //设置视频的描述
        video.setVideo_describe(mEtDisc.getText().toString());
        //设置视频发布的语义化地址
        video.setVideo_locaton(mBtn.getText().toString());
        //进行数据保存
        video.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                //取消进度条
                mReleaseView.canelLoadingDialog();
                if (e == null) {
                    mReleaseView.showMsg("分享视频成功!");
                    LogUtils.i("myTag", "分享视频成功!");
                } else {
                    mReleaseView.showMsg("分享视频失败!" + e.getLocalizedMessage());
                    LogUtils.i("myTag", "分享视频失败!保存用户信息出现错误!" + e.toString());
                }
            }
        });
    }

    @Override
    public void unBind() {

    }

    /**
     * Bitmap对象保存味图片文件
     *
     * @param bitmap
     */
    private File saveBitmapFile(Bitmap bitmap) {
        File file = new File("/mnt/sdcard/Pike/" + System.currentTimeMillis() + ".jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
