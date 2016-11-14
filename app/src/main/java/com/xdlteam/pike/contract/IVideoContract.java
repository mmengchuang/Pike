package com.xdlteam.pike.contract;



import android.widget.ListView;

import com.xdlteam.pike.base.BasePresenter;
import com.xdlteam.pike.base.BaseView;
import com.xdlteam.pike.bean.Video;

import cn.bmob.v3.exception.BmobException;
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

        ListView getmLv();

        Video getmVideo();
    }
    public interface IVideoPresenter extends BasePresenter {
        void initData();

        /**
         * 播放视频的方法
         * @param videoUrl 视频资源的地址
         */
        void playfunction(String videoUrl);

        /**
         * 查询信息成功
         */
        void queryUserSuccess();

        /**
         * 查询信息失败
         * @param e
         */
        void queryUseError(BmobException e);

        /**
         * 显示评论的内容
         */
        void showDiscuss();

        /**
         * 查询视频内容
         * @param objectId
         */
        void queryDiscuss(String objectId);
    }
}
