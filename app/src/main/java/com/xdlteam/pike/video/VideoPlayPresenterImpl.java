package com.xdlteam.pike.video;

import android.widget.ListView;

import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.Discuss;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.contract.IVideoContract;
import com.xdlteam.pike.util.LogUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import static cn.bmob.v3.BmobRealTimeData.TAG;

/**
 * 小视频播放的处理类
 * Created by 11655 on 2016/11/7.
 */

public class VideoPlayPresenterImpl implements IVideoContract.IVideoPresenter {
    //视图层对象
    private IVideoContract.IVideoView mView;
    //显示视频的控件
    private VideoView mVideoView;
    //视频控制器对象
    private MediaController controller;
    private ListView mXlvPl;
    private Video mVideoBean;

    public VideoPlayPresenterImpl(IVideoContract.IVideoView mView) {
        this.mView = mView;
    }

    @Override
    public void initData() {
        //获取控件
        mVideoView = mView.getVideoView();
        mXlvPl  = mView.getmLv();
        //获取当前选中的视频对象
        mVideoBean = mView.getmVideo();
        //设置控制器
        controller = new MediaController(MyApplcation.context);
        mVideoView.setMediaController(controller);
    }

    /**
     * 播放视频的方法
     * @param videoUrl  视频资源路径
     */
    @Override
    public void playfunction(String videoUrl){
        mVideoView.setVideoPath(videoUrl);//设置播放地址
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        mVideoView.requestFocus();//取得焦点
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                //设置视频缓存区
                mediaPlayer.setBufferSize(512 * 1024);
            }
        });
    }

    @Override
    public void queryUserSuccess() {

    }

    @Override
    public void queryUseError(BmobException e) {

    }

    @Override
    public void showDiscuss() {

    }

    @Override
    public void queryDiscuss(String objectId) {
        LogUtils.i(TAG, "查询评论的参数为" + objectId);
        BmobQuery<Discuss> query = new BmobQuery<>();
        //添加查询条件
        query.addWhereEqualTo("goodsId", objectId + "");
        query.findObjects(new FindListener<Discuss>() {
            @Override
            public void done(List<Discuss> list, BmobException e) {
                if (e == null) {
                    LogUtils.i(TAG, "评论查询成功");
                    LogUtils.i(TAG, "评论信息为" + list.toString());
                    //加载成功，设置适配器
                    mXlvPl.setAdapter(new DiscussXlvAdapter(list,
                            MyApplcation.context, VideoPlayPresenterImpl.this));
                } else {
                    LogUtils.i(TAG, "查询数据失败" + e.toString());
                }
            }
        });
    }

    @Override
    public void unBind() {

    }
}
