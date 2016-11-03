package com.xdlteam.pike.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 所有Activity的基类，封装了常用的方法
 *
 * @author mmengchen
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
    }


    //解绑view
    protected abstract void unBind();
}
