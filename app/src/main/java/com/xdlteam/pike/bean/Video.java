package com.xdlteam.pike.bean;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import cn.bmob.v3.BmobACL;
import com.squareup.picasso.Picasso;

import java.net.URL;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

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
    private BmobGeoPoint video_point;//视频发布地理位置
    private String video_locaton;//视频发布的地点描述
    private BmobFile video_content;//视频内容
    private String video_describe;//视频描述

    public BmobGeoPoint getVideo_point() {
        return video_point;
    }

    public void setVideo_point(BmobGeoPoint video_point) {
        this.video_point = video_point;
    }

    public BmobFile getVideo_content() {
        return video_content;
    }

    public void setVideo_content(BmobFile video_content) {
        this.video_content = video_content;
    }

    public String getVideo_describe() {
        return video_describe;
    }

    public void setVideo_describe(String video_describe) {
        this.video_describe = video_describe;
    }

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

    public String getVideo_locaton() {
        return video_locaton;
    }

    public void setVideo_locaton(String video_locaton) {
        this.video_locaton = video_locaton;
    }

}
