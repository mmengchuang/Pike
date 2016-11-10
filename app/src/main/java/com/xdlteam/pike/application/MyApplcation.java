package com.xdlteam.pike.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.config.Contracts;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.DeviceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

/**
 * 自定义的全部的Applcation类
 * Created by 11655 on 2016/10/18.
 */

public class MyApplcation extends Application {

    //应用签名 9d79981a43dd5e541f1f2e4f42bb0809


    public static User sUser;
    private static Map<String, Object> dataMaps;
    public static Context context;

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
        dataMaps = new HashMap<>();
        context = getApplicationContext();


        //初始化VCCamera
        // 设置拍摄视频缓存路径
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/Camera/Pike/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/Pike/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/Camera/Pike/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
//        VCamera.initialize(getApplicationContext());

    }

    /**
     * 保存信息
     *
     * @param key
     * @param values
     */
    public static void putDatas(String key, Object values) {
        dataMaps.put(key, values);
    }

    /**
     * 获取数据
     *
     * @param key
     * @param flag
     * @return
     */
    public static Object getDatas(String key, boolean flag) {
        if (flag) {
            return dataMaps.remove(key);
        }
        return dataMaps.get(key);
    }

}
