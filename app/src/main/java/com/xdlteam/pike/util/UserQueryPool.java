package com.xdlteam.pike.util;

import android.support.v4.util.Pools;

import com.xdlteam.pike.bean.User;

import cn.bmob.v3.BmobQuery;


/**
 * Created by Yin on 2016/11/7.
 */

public class UserQueryPool {
	private static final Pools.SynchronizedPool<BmobQuery<User>> sPool =
			new Pools.SynchronizedPool<>(10);

	public static BmobQuery<User> obtain() {
		BmobQuery<User> instance= sPool.acquire();
		return (instance != null) ? instance : new BmobQuery<User>();
	}

	public static void recycle(BmobQuery<User> query) {
		// Clear state if needed.
		sPool.release(query);
	}
}
