package com.xdlteam.pike.bean;

import java.util.ArrayList;

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
    private ArrayList<String> userShouCang;
    private ArrayList<String> userGuanZhu;

    public ArrayList<String> getUserShouCang() {
        if(userShouCang==null){
            userShouCang=new ArrayList<>();
        }
        return userShouCang;
    }

    public void setUserShouCang(ArrayList<String> userShouCang) {
        this.userShouCang = userShouCang;
    }

    public ArrayList<String> getUserGuanZhu() {
        if(userGuanZhu==null){
            userGuanZhu=new ArrayList<>();
        }
        return userGuanZhu;
    }

    public void setUserGuanZhu(ArrayList<String> userGuanZhu) {
        this.userGuanZhu = userGuanZhu;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public BmobFile getUserHeadPortrait() {
        if(userHeadPortrait==null){
            userHeadPortrait = new BmobFile();
        }
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
