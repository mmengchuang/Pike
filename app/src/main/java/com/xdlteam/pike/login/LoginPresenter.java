package com.xdlteam.pike.login;

import com.xdlteam.pike.base.BasePresenter;

/**
 * Created by Yin on 2016/11/2.
 */

public class LoginPresenter implements BasePresenter {
    private LoginActivity mLoginActivity;
    public LoginPresenter(LoginActivity loginActivity) {
        mLoginActivity=loginActivity;
    }
    @Override
    public void unBind() {
        mLoginActivity=null;
    }

    public void getUser() {

    }
}
