package com.xdlteam.pike.home;

import android.os.Bundle;

import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void unBind() {

    }
}
