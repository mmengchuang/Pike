package com.xdlteam.pike.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xdlteam.pike.R;

import org.json.JSONObject;

public class QQLoginActivity extends AppCompatActivity {

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

    private void initViews() {
    }

    private class BaseUiListener implements IUiListener {

        @Override

        public void onComplete(Object response) {

//            mBaseMessageText.setText("onComplete:");
//
//            mMessageText.setText(response.toString());
            JSONObject json=(JSONObject)response;
            doComplete(json);
            Log.i("MyTag",json+"");

        }

        protected void doComplete(JSONObject values) {
        }
        @Override

        public void onError(UiError e) {

            Log.i("MyTag", "code:" + e.errorCode + ", msg:"

                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override

        public void onCancel() {

            Log.i("MyTag", "onCancel");

        }
    }

    public void login()
    {
        mTencent = Tencent.createInstance("101359231", this.getApplicationContext());
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, SCOPE, new BaseUiListener());
//            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}
