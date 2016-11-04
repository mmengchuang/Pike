package com.xdlteam.pike.contract;

import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.xdlteam.pike.base.BasePresenter;
import com.xdlteam.pike.base.BaseView;

/**
 * 契约类、使view 和 Presenter 之前的方法清晰
 * Created by 11655 on 2016/10/18.
 */

public class IReleaseContract {
    public interface IReleaseView extends BaseView{
       /**
        * Toast数据
        * @param msg
        */
       void showMsg(String msg);

        /**
         * 加载提示框
         */
        void showLoadingDialog();

        /**
         * 取消加载提示框
         */
        void dismissProcessDialog();

       /**
        * activity的跳转
        */
       void jumpActivity();

        ImageView getmActReleaseIvFirstVideo();

        GridView getmActReleaseGv();

        EditText getmActReleaseEtContent();

        Button getmActReleaseBtnLocation();

    }
    public interface IReleasePresenter extends BasePresenter {

        /**
         * 初始化相关数据
         */
        void initData();

        /**
         * 设置相关数据
         * @param datas 数据
         */
        void setDatas(String datas);

        /**
         * 发布视频
         */
        void release();
    }
}
