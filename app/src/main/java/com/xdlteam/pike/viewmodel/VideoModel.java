package com.xdlteam.pike.viewmodel;

import android.databinding.BindingAdapter;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Yin on 2016/11/7.
 */

public class VideoModel {
	private BmobQuery<Video> mVideoBmobQuery;
	private static final int limit = 3;//每次查询数量
	private static Random sRandom = new Random();

	public VideoModel() {
		this.mVideoBmobQuery = new BmobQuery<>();
	}

	public Observable<List<Video>> getVideos(int spik) {
		mVideoBmobQuery.order("-createdAt");
		return mVideoBmobQuery.findObjectsObservable(Video.class);
	}


	@BindingAdapter({"imageUrl"})
	public static void loadImage(ImageView view, String imageUrl) {
		Picasso.with(view.getContext())
				.load(imageUrl)
				.into(view);
	}

	@BindingAdapter("imageVideo")
	public static void imageVideo(final ImageView view, String userId) {
		BmobQuery<User> query = new BmobQuery<>();
		query.getObjectObservable(User.class, userId)
				.subscribe(new Action1<User>() {
					@Override
					public void call(User user) {
						Picasso.with(view.getContext())
								.load(user.getUserHeadPortrait().getFileUrl())
								.into(view);
					}
				});
	}

}
