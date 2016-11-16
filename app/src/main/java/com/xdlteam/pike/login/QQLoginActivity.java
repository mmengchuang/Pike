package com.xdlteam.pike.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

public class QQLoginActivity extends AppCompatActivity {
    static final String TAG = QQLoginActivity.class.getSimpleName();
    Tencent mTencent;
    private static final String SCOPE = "get_user_info, get_simple_userinfo, add_share";// 权限：读取用户信息并分享信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqlogin);
//        mTencent = Tencent.createInstance("101359231", this.getApplicationContext());
        initViews();
        login();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11101 && resultCode == -1) {
            LogUtils.i(TAG, "QQ授权成功!");
            LogUtils.i(TAG, "QQ授权获取到的数据：" + data.getStringExtra("key_response"));
            try {
                JSONObject jsonObject = new JSONObject(data.getStringExtra("key_response"));
                String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(
                        BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ, token, expires, openId);
                loginWithAuth(authInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initViews() {
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Log.i(TAG, "onComplete");
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
    }

    public void login() {
        if (mTencent == null) {
            //mTencent = Tencent.createInstance("101359231", this.getApplicationContext());
            mTencent = Tencent.createInstance(Contracts.QQ_APP_ID, this.getApplicationContext());
        }
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", new BaseUiListener());
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
                    Toast.makeText(QQLoginActivity.this,"登陆成功!",Toast.LENGTH_SHORT).show();
                    //将用户信息保存到内存中
                    MyApplcation.sUser = BmobUser.getCurrentUser(User.class);
                    Intent intent = new Intent(QQLoginActivity.this, HomeActivity.class);
                    intent.putExtra("json", jsonObject.toString());
                    intent.putExtra("from", auth.getSnsType());
                    startActivity(intent);
                    QQLoginActivity.this.finish();
                } else {
                    Toast.makeText(QQLoginActivity.this, "登陆失败\r\n" + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
