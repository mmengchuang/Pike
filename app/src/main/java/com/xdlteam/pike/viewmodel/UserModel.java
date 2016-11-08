package com.xdlteam.pike.viewmodel;

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
	private static final int LIMIT =10;
	public User getUser() {
		mUser=BmobUser.getCurrentUser(User.class);
		return mUser;
	}

	
	public Observable<List<Video>> getVideos(int spik,String userId) {
		BmobQuery<Video> query = new BmobQuery<>();
		query.order("-createdAt");
		query.setSkip(spik);
		query.setLimit(LIMIT);
		query.addWhereEqualTo("userId", userId);
		return query.findObjectsObservable(Video.class);
	}


}
