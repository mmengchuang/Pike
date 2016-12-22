package com.xdlteam.pike.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xdlteam.pike.R;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.config.Contracts;
import com.xdlteam.pike.home.HomeActivity;
import com.xdlteam.pike.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.OkHttpClient;

public class QQLoginActivity extends AppCompatActivity {
    static final String TAG = QQLoginActivity.class.getSimpleName();
    Tencent mTencent;
    private static final String SCOPE = "all";// 权限：读取用户信息并分享信息
    private OkHttpClient mOkHttpClient;
    private IUiListener userInfoListener; //获取用户信息监听器
    private IUiListener loginListener;//授权信息监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqlogin);
        initViews();
        initDatas();
        login();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                LogUtils.i(TAG, "QQ授权成功!");
                doComplete((JSONObject) response);
            }

            @Override

            public void onError(UiError e) {
                Log.i(TAG, "-->code:" + e.errorCode + ", msg:"
                        + e.errorMessage + ", detail:" + e.errorDetail);
            }

            @Override

            public void onCancel() {
                Log.i("myTag", "onCancel");
            }
        };

        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub

            }

            /**
             * 返回用户信息样例
             *
             * {"is_yellow_year_vip":"0","ret":0,
             * "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/40",
             * "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"",
             * "city":"黄冈","
             * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/50",
             * "vip":"0","level":"0",
             * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "province":"湖北",
             * "is_yellow_vip":"0","gender":"男",
             * "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/30"}
             */
            @Override
            public void onComplete(Object arg0) {
                // TODO Auto-generated method stub
                if (arg0 == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    LogUtils.i(TAG, "json=" + String.valueOf(jo));
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");
                    //将用户信息保存到表中
                    User user = new User();
                    user.setUserNick(nickName);
                    user.setUserSex(gender);
                    BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
                    user.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //将用户信息保存内存中
                                MyApplcation.sUser = BmobUser.getCurrentUser(User.class);
                                startActivity(new Intent(QQLoginActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(QQLoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViews() {
    }

    //发起登陆授权
    public void login() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Contracts.QQ_APP_ID, this.getApplicationContext());
        }
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, SCOPE, loginListener);
        }
    }

    //获取到授权信息的处理
    protected void doComplete(JSONObject values) {
        try {
            String token = values.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            String expires = values.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            String openId = values.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
            LogUtils.i(TAG, "信息为" + token + "," + expires + "," + openId);
            BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(
                    BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ, token, expires, openId);
            loginWithAuth(authInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第三方登陆
     *
     * @param auth
     */
    public void loginWithAuth(final BmobUser.BmobThirdUserAuth auth) {
        BmobUser.loginWithAuthData(auth, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject jsonObject, BmobException e) {
                if (e == null) {
                    LogUtils.i(TAG, "loginwithauth --登陆成功!" + jsonObject.toString());
                    //获取QQ用户信息
                    getQQInfo(jsonObject);
                } else {
                    LogUtils.e(TAG, "登陆失败\r\n" + e.getLocalizedMessage());
                    Toast.makeText(QQLoginActivity.this, "登陆失败\r\n" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取QQ的信息
     *
     * @param
     * @param jsonObject
     * @return void
     * @throws
     * @Title: getQQInfo
     * @Description: TODO
     */
    public void getQQInfo(JSONObject jsonObject) {
        //解析出相关参数
        //{"qq":{"expires_in":7776000,"access_token":"C5254743BF72BAD47D5157474AD0FD40","openid":"C44B701C58944AB3B514B62EF9A81532"}}
        try {
            JSONObject object = jsonObject.getJSONObject("qq");
            final int expires_in = object.getInt("expires_in");
            final String access_token = object.getString("access_token");
            final String openid = object.getString("openid");
            LogUtils.i(TAG, "解析出相关参数" + expires_in + "  ," + access_token + " ," + openid);
            String str = expires_in + "";
            //**下面这两步设置很重要,如果没有设置,返回为空**
            mTencent.setOpenId(openid);
            mTencent.setAccessToken(access_token, str);
            //获取用户信息
            UserInfo userInfo = new UserInfo(QQLoginActivity.this, mTencent.getQQToken());
            userInfo.getUserInfo(userInfoListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
