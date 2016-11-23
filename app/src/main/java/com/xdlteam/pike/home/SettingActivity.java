package com.xdlteam.pike.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.xdlteam.pike.R;
import com.xdlteam.pike.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Created by 11655 on 2016/11/23.
 */

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.act_setting_iv_back)
    ImageButton mActSettingIvBack;
    @BindView(R.id.act_setting_btn_clear)
    Button mActSettingBtnClear;
    @BindView(R.id.act_setting_btn_exit)
    Button mActSettingBtnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.act_setting_iv_back, R.id.act_setting_btn_clear, R.id.act_setting_btn_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_setting_iv_back:
                finish();
                break;
            case R.id.act_setting_btn_clear:
                clearVideoCache();
                break;
            case R.id.act_setting_btn_exit:
                loginOut();
                break;
        }
    }

    /**
     * 清除缓存
     */
    private void clearVideoCache() {

    }

    /**
     * 退出登录
     */
    private void loginOut() {
        BmobUser.logOut();   //清除缓存用户对象
        startActivity(new Intent(this, LoginActivity.class));//返回到登陆页面
        finish();
    }
}
