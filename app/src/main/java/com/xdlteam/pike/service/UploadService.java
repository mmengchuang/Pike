package com.xdlteam.pike.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.xdlteam.pike.R;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.util.LogUtils;
import com.xdlteam.pike.util.NotificationUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.xdlteam.pike.application.MyApplcation.context;

/**
 * 上传视频的服务
 * Created by 11655 on 2016/11/7.
 */

public class UploadService extends Service {
    private NotificationUtils mNotificationUtils;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("myTag", "onBind");
        return new MyIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("myTag", "onCreate");
        mNotificationUtils = new NotificationUtils(this);
    }

    //服务的内部类(调用服务中的方法)
    public class MyIBinder extends Binder {
        public void uploadFiles(String... urls) {
            UploadService.this.uploadFiles(urls);
        }
    }

    /**
     * 上传图片和视频到服务器
     *
     * @param urls 图片和视频的路径和视频描述和地址
     */
    private void uploadFiles(String... urls) {
        //开启进度条通知栏和提醒
//        showNotifiation("视频分享中");
        mNotificationUtils.showNotification(1000);
        showNotifiation("视频分享中...");
        //获取第一帧图片的路径
        String filePathImg = urls[0];
        LogUtils.i("myTag", "我获取到的文件路径" + filePathImg);
        //获取视频的路径
        String filePathVideo = urls[1];
        //获取视频描述
        final String videoDesc = urls[2];
        //获取地理位置
        final String locationStr = urls[3];
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
                    //保存用户数据
                    saveVideos(files.get(0), files.get(1), videoDesc, locationStr);
                    //取消通知栏上传进度
                    mNotificationUtils.cancel(1000);
                    showNotifiation("视频分享成功!");
                    //停止服务
                    stopSelf();
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
                mNotificationUtils.updateProgress(1000,curPercent);
            }
        });
    }

    /**
     * 保存视频到数据库
     *
     * @param bmobImgFile  上传后的视频的第一帧图片
     * @param bmobVieoFile 上传后的视频文件
     */
    private void saveVideos(BmobFile bmobImgFile, BmobFile bmobVieoFile, String videoDesc, String locationStr) {
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
        mVideo.setVideo_describe(videoDesc);
        //设置视频发布的语义化地址
        mVideo.setVideo_locaton(locationStr);
        //进行数据保存
        mVideo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                //隐藏普通进度条
                if (e == null) {
                    LogUtils.i("myTag", "分享视频成功!");
                } else {
                    LogUtils.i("myTag", "分享视频失败!保存用户信息出现错误!" + e.toString());
                }
            }
        });
    }

    private void showNotifiation(String msg) {
        //构造一个通知栏构建者对象
        Notification.Builder builder = new Notification.Builder(context);
        // 通知栏
        builder.setSmallIcon(R.mipmap.ic_launcher)// 设置消息的图标
                .setTicker("视频分享中....")// //设置消息在Activity显示的标记(标题)
                .setContentTitle("草坪拍客")// 设置消息的标题
                .setContentText(msg)// 设置消息的内容
                .setWhen(System.currentTimeMillis())// 设置系统时间
                // 震动,声音,闪光灯....
                .setDefaults(Notification.DEFAULT_ALL);
        Intent intent = new Intent();
        // 未来意图
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), // 上下文
                1, // 请求码
                intent, // 意图
                0//
        );
        // 当用户点击消息时,需要触发未来意图
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true);// 悬挂
        Notification notification = builder.build();
        // 设置消息的清除方式
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 不能清除
        // 获取通知栏管理器
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        /**
         * 当前通知的唯一标识 为了更好的管理通知
         */
        manager.notify(1, notification);
    }

}
