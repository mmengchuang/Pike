package com.xdlteam.pike.application;

import android.app.Application;

import com.xdlteam.pike.config.Contracts;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

/**
 * 自定义的全部的Applcation类
 * Created by 11655 on 2016/10/18.
 */

public class MyApplcation extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化BMob 短信服务SDK
        BmobSMS.initialize(getApplicationContext(), Contracts.BMOB_APP_KEY);
        //初始化BMob 数据SDK
        Bmob.initialize(this, Contracts.BMOB_APP_KEY);
        //初始化Bmob 支付SDK
        //Bmob自动更新,这里只用一次
        //BmobUpdateAgent.initAppVersion();
    }
}
