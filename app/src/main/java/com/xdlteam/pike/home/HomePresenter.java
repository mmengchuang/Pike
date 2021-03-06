package com.xdlteam.pike.home;

/**
 * Created by Yin on 2016/11/3.
 */

import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import rx.Observable;
import rx.Single;

/**
 * 主页
 */
public class HomePresenter {
    private HomeActivity mHomeActivity;

    public HomePresenter(HomeActivity homeActivity) {
        mHomeActivity = homeActivity;
    }

    public Single<User> getUser() {
        return Single.just(BmobUser.getCurrentUser(User.class));
    }

}
