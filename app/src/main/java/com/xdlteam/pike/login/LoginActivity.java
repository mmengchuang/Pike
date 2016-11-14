package com.xdlteam.pike.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.home.HomeActivity;
import com.xdlteam.pike.register.RegisterActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity {
    private LoginPresenter mLoginPresenter;
    private EditText etUserName,etPwd;
    private Button btnLogin;
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
        etUserName= (EditText) findViewById(R.id.act_login_username);
        etPwd= (EditText) findViewById(R.id.act_login_pwd);
//        btnLogin= (Button) findViewById(R.id.act_login_btn);
    }
    //初始化数据
    private void initDatas() {
        SharedPreferences sp=getSharedPreferences("userinfo.txt",MODE_PRIVATE);
        String userName = sp.getString("userName", null);
        String pwd = sp.getString("pwd", null);
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            return;
        }
        etUserName.setText(userName);
        etPwd.setText(pwd);
    }
    //初始化视图操作
    private void initOpers() {

    }
    //登录按钮点击事件
    public void loginClick(View v){
        final String userName=etUserName.getText().toString().trim();//获取用户名

        final String pwd=etPwd.getText().toString().trim();//获取密码
        //判断用户名或者密码是否为空
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //进行登录
        User.loginByAccount(userName, pwd, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e!=null){
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = HomeActivity.newIntent(LoginActivity.this);
                startActivity(intent);
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                LoginActivity.this.finish();
//                if(user!=null){
//                    SharedPreferences sp=getSharedPreferences("userinfo.txt",MODE_PRIVATE);
//                    SharedPreferences.Editor edit = sp.edit();
////                    edit.putString("userName",userName);
////                    edit.putString("pwd",pwd);
////                    edit.commit();
//                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
//
//                }
            }
        });

    }
    //注册按钮点击事件
    public void registerClick(View v){
        //跳转至注册页面
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    protected void unBind() {
//        mLoginPresenter.unBind();
    }

    public void qqClick(View v){
        //跳转至qq登录页面
        startActivity(new Intent(LoginActivity.this,QQLoginActivity.class));
    }

}
