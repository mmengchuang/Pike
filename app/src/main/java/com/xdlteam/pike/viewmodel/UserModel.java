package com.xdlteam.pike.viewmodel;

import android.databinding.ObservableArrayList;

import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import rx.Observable;

/**
 * Created by Yin on 2016/11/8.
 */

public class UserModel {
	private User mUser;//当前用户
	private int limit=5;
	public User getUser() {
		mUser=BmobUser.getCurrentUser(User.class);
		return mUser;
	}

	public Observable<List<Video>> getVideos(int spik) {
		BmobQuery<Video> query = new BmobQuery<>();
		query.order("-createdAt");
		query.setSkip(spik);
		query.setLimit(limit);
		query.addWhereEqualTo("userId", mUser.getObjectId());
		return query.findObjectsObservable(Video.class);
	}
}
