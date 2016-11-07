package com.xdlteam.pike.contract;



import com.xdlteam.pike.base.BasePresenter;
import com.xdlteam.pike.base.BaseView;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by 11655 on 2016/11/7.
 */

public class IVideoContract {
    public interface IVideoView extends BaseView {
        /**
         * 获取视频的控件
         * @return
         */
        VideoView getVideoView();
    }
    public interface IVideoPresenter extends BasePresenter {
        void initData();

        /**
         * 播放视频的方法
         * @param videoUrl 视频资源的地址
         */
        void playfunction(String videoUrl);
    }
}
