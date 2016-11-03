package com.xdlteam.pike.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户信息Bean类
 * @author mmengchen
 */

public class User extends BmobUser {
    private String userNick;
    private BmobFile userHeadPortrait;
    private String userSex;

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public BmobFile getUserHeadPortrait() {
        return userHeadPortrait;
    }

    public void setUserHeadPortrait(BmobFile userHeadPortrait) {
        this.userHeadPortrait = userHeadPortrait;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }
}
