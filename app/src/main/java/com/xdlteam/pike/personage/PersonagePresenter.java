package com.xdlteam.pike.personage;

import com.xdlteam.pike.bean.User;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;

/**
 * Created by Yin on 2016/11/7.
 */

public class PersonagePresenter {
	private WeakReference<PersonageActivity> mActivity;

	public PersonagePresenter(PersonageActivity activity) {
		mActivity = new WeakReference<PersonageActivity>(activity);
	}

	public User getUser() {
		return BmobUser.getCurrentUser(User.class);
	}

	public PersonageActivity getView() {
		if (mActivity != null && mActivity.isEnqueued()) {
			return mActivity.get();
		}
		return null;
	}
}
