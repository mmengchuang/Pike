package com.xdlteam.pike.config;

/**
 * 默认配置的属性值
 *
 * @author mmengchen
 */
public interface Contracts {
    //设置一个标记,true 为debug状态
    boolean DEBUG = true;

    //Bmob云APP_Key

//    String BMOB_APP_KEY = "e9bbb016bbc3450ec1449b45f366174a";
    //我的Key
        String BMOB_APP_KEY = "bdede6a9c29e3124ae6b8666d376c8b0";

    //微信分享key
    String WX_APP_ID = "";

    //QQ登陆Key
    String QQ_APP_ID = "101361227";

    /* 默认头像地址 */
    String DEFAULT_HEADE_URI = "http://bmob-cdn-6590.b0.upaiyun.com/2016/10/16/22901ee0406f7af280b56a1b5d555f58.png";

    /*默认视频录制位置*/
    String DEFAULT_VIDEO_CACHE = "/sdcard/Movies";
}
