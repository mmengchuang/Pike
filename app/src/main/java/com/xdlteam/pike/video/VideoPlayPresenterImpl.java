package com.xdlteam.pike.video;

import com.xdlteam.pike.contract.IVideoContract;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * 小视频播放的处理类
 * Created by 11655 on 2016/11/7.
 */

public class VideoPlayPresenterImpl implements IVideoContract.IVideoPresenter {
    //视图层对象
    private IVideoContract.IVideoView mView;
    //显示视频的控件
    private VideoView mVideoView;
    public VideoPlayPresenterImpl(IVideoContract.IVideoView mView) {
        this.mView = mView;
    }

    @Override
    public void initData() {
        //获取控件
       mVideoView = mView.getVideoView();

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
    public void unBind() {

    }
}
