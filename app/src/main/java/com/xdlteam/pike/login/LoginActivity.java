package com.xdlteam.pike.login;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.home.HomeActivity;
import com.xdlteam.pike.register.RegisterActivity;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity implements Animator.AnimatorListener{
    private LoginPresenter mLoginPresenter;
    private EditText etUserName, etPwd;
    private CircularProgressButton mBtnLogin;
    //用于判断成功和失败，false 表示失败 true 表示成功
    private boolean loginFlag  = false;
    private BmobException exception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_login1);
//        mLoginPresenter = new LoginPresenter(this);
        initView();
        initDatas();
        initOpers();
    }

    //初始化视图组件
    private void initView() {
        etUserName = (EditText) findViewById(R.id.act_login_username);
        etPwd = (EditText) findViewById(R.id.act_login_pwd);
        mBtnLogin = (CircularProgressButton) findViewById(R.id.act_login_btn);
    }

    //初始化数据
    private void initDatas() {
    /*    SharedPreferences sp=getSharedPreferences("userinfo.txt",MODE_PRIVATE);
        String userName = sp.getString("userName", null);
        String pwd = sp.getString("pwd", null);
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            return;
        }
        etUserName.setText(userName);
        etPwd.setText(pwd);*/
    }

    //初始化视图操作
    private void initOpers() {

    }

    //登录按钮点击事件
    public void loginClick(View v) {
        final String userName = etUserName.getText().toString().trim();//获取用户名

        final String pwd = etPwd.getText().toString().trim();//获取密码
        //判断用户名或者密码是否为空
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBtnLogin.getProgress()==0){
            //进行登录
            User.loginByAccount(userName, pwd, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e!=null){//登陆失败
                        exception = e;
                        simulateErrorProgress(mBtnLogin);
                        loginFlag = false;
                        return;
                    } else {
                        simulateSuccessProgress(mBtnLogin);
                        loginFlag = true;
                    }
                }
            });
        }else {
            mBtnLogin.setProgress(0);
        }


    }

    //注册按钮点击事件
    public void registerClick(View v) {
        //跳转至注册页面
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    protected void unBind() {
//        mLoginPresenter.unBind();
    }

    public void qqClick(View v) {
        //跳转至qq登录页面
        startActivity(new Intent(LoginActivity.this, QQLoginActivity.class));
    }

    /**
     * 显示登陆成功的进度
     *
     * @param button
     */
    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(3000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
        widthAnimation.addListener(LoginActivity.this);
    }

    /**
     * 显示登陆失败的进度条
     * @param button
     */
    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(3000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
//        widthAnimation.addListener(LoginActivity.this);
        widthAnimation.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (loginFlag){
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = HomeActivity.newIntent(LoginActivity.this);
            startActivity(intent);
            LoginActivity.this.finish();
        }else {//失败时
            Toast.makeText(LoginActivity.this, "登录失败\r\n"+exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
