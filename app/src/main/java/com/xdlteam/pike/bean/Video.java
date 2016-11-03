package com.xdlteam.pike.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Yin on 2016/11/3.
 */

/**
 * 小视频的bean
 */
public class Video extends BmobObject {
    private String userId;//视频是谁发布的
    private int loveCount;//点赞的数量,红心的数量
    private BmobFile video_image;//视频的缩略图

    public BmobFile getVideo_image() {
        return video_image;
    }

    public void setVideo_image(BmobFile video_image) {
        this.video_image = video_image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }
}
