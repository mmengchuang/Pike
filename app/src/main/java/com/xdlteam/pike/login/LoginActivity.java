package com.xdlteam.pike.login;

import android.os.Bundle;
import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;

public class LoginActivity extends BaseActivity {
    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.getUser();
    }

    @Override
    protected void unBind() {
        mLoginPresenter.unBind();
    }
}
