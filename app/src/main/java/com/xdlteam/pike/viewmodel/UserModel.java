package com.xdlteam.pike.viewmodel;

import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Yin on 2016/11/8.
 */

public class UserModel {
	private User mUser;//当前用户

	public User getUser() {
		mUser = BmobUser.getCurrentUser(User.class);
		return mUser;
	}


	public Observable<List<Video>> getVideos(String userId) {
		BmobQuery<Video> query = new BmobQuery<>();
		query.order("-createdAt");
		query.addWhereEqualTo("userId", userId);
		return query.findObjectsObservable(Video.class);
	}

	public Observable<List<Video>> getVideos(List<String> xihuans) {
		return Observable.from(xihuans)
				.flatMap(new Func1<String, Observable<Video>>() {
					@Override
					public Observable<Video> call(String s) {
						BmobQuery<Video> query = new BmobQuery<>();
						return query.getObjectObservable(Video.class, s);
					}
				})
				.toList();
	}

	/**
	 * 粉丝的查询
	 * @param userId
	 * @return
	 */
	public Observable<Integer> getFensiCount(final String userId) {
		BmobQuery<User> query = new BmobQuery<>();
		return query.findObjectsObservable(User.class)
				.subscribeOn(Schedulers.io())
				.flatMap(new Func1<List<User>, Observable<User>>() {
					@Override
					public Observable<User> call(List<User> users) {
						return Observable.from(users);
					}
				})
				.filter(new Func1<User, Boolean>() {
					@Override
					public Boolean call(User user) {
						return user.getUserGuanZhu().contains(userId);
					}
				})
				.count()
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * 关注的查询
	 * @param list
	 * @return
	 */
	public Observable<List<Video>> getGuanZhu(List<String> list) {
		return Observable.from(list)
				.flatMap(new Func1<String, Observable<User>>() {
					@Override
					public Observable<User> call(String s) {
						BmobQuery<User> query = new BmobQuery<User>();
						return query.getObjectObservable(User.class, s);
					}
				})
				.flatMap(new Func1<User, Observable<List<Video>>>() {
					@Override
					public Observable<List<Video>> call(User user) {
						BmobQuery<Video> query = new BmobQuery<Video>();
						query.addWhereEqualTo("userId", user.getObjectId());
						return query.findObjectsObservable(Video.class);
					}
				})
				.flatMap(new Func1<List<Video>, Observable<Video>>() {
					@Override
					public Observable<Video> call(List<Video> videos) {
						return Observable.from(videos);
					}
				})
				.toList()
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Observable<List<Video>> getAllVideos() {
		BmobQuery<Video> query = new BmobQuery<>();
		return query.findObjectsObservable(Video.class)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}
}
