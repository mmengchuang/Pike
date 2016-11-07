package com.xdlteam.pike.release;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.contract.IReleaseContract;
import com.xdlteam.pike.util.LogUtils;
import com.xdlteam.pike.util.VideoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

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
    //进度条
    private int progress = 0;

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
        LogUtils.i("myTag", "点击发布按钮!");
        //显示普通进度条
        mReleaseView.showLoadingDialog();
        //获取第一帧图片的文件
        File imgFile = saveBitmapFile(mBitmap);
        //获取第一帧图片的路径
        String filePathImg = imgFile.getPath();
        LogUtils.i("myTag", "我获取到的文件路径" + filePathImg);
        //获取视频的路径
        String filePathVideo = mVideoURL;
        final String[] filePaths = new String[2];
        filePaths[0] = filePathImg;
        filePaths[1] = filePathVideo;
        //批量上传文件
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                    //do something
                    LogUtils.i("myTag", "文件上传成功!");
                    saveVideos(files.get(0), files.get(1));
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                LogUtils.e("myTag", "错误码" + statuscode + ",错误描述：" + errormsg);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
                LogUtils.i("myTag", "当前第" + curIndex + " 文件正在上传," + "当前上传文件的进度 " + curPercent +
                        ",总的上传文件数 " + total + "总的上传进度 " + totalPercent);
            }
        });
    }

    /**
     * 保存视频到数据库
     *
     * @param bmobImgFile  上传后的视频的第一帧图片
     * @param bmobVieoFile 上传后的视频文件
     */
    private void saveVideos(BmobFile bmobImgFile, BmobFile bmobVieoFile) {
        //显示普通进度
//        mReleaseView.showLoadingDialog();
        LogUtils.i("myTag", "我在进行保存操作");
        //更新数据表
        com.xdlteam.pike.bean.Video mVideo = new Video();
        //设置发布小视频的用户ID,需要更改,测试时使用固定的
        mVideo.setUserId(BmobUser.getCurrentUser().getObjectId());
        //设置图片的缩略图
        mVideo.setVideo_image(bmobImgFile);
        //设置视频发送的地点
        BDLocation bdLocation = (BDLocation) MyApplcation.getDatas("location",
                true);
        mVideo.setVideo_point(new BmobGeoPoint(bdLocation.getLongitude(),
                bdLocation.getLatitude()));
        //设置视频文件
        mVideo.setVideo_content(bmobVieoFile);
        //设置视频的描述
        mVideo.setVideo_describe(mEtDisc.getText().toString());
        //设置视频发布的语义化地址
        mVideo.setVideo_locaton(mBtn.getText().toString());
        //进行数据保存
        mVideo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                //隐藏普通进度条
                mReleaseView.dismissProcessDialog();
                if (e == null) {
                    mReleaseView.showMsg("分享视频成功!");
                    LogUtils.i("myTag", "分享视频成功!");
                    mReleaseView.jumpActivity();
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
        if (!file.exists()) {//文件不存在，则创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
