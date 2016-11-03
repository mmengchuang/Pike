package com.xdlteam.pike.application;

import android.app.Application;

import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.config.Contracts;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

/**
 * 自定义的全部的Applcation类
 * Created by 11655 on 2016/10/18.
 */

public class MyApplcation extends Application {

    //应用签名 9d79981a43dd5e541f1f2e4f42bb0809


    public static User sUser;

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
