package com.xdlteam.pike.register;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xdlteam.pike.R;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.base.BaseActivity;
import com.xdlteam.pike.bean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends BaseActivity {


    //头像
    @BindView(R.id.act_register_iv_touxiang)
    CircleImageView mIvTouxiang;
    //电话输入框
    @BindView(R.id.act_register_et_phone)
    EditText mEtPhone;
    //密码输入框
    @BindView(R.id.act_register_et_pwd)
    EditText mEtPwd;
    //验证码输入框
    @BindView(R.id.act_register_et_yanzheng)
    EditText mEtYanzheng;
    //性别显示
    @BindView(R.id.act_register_tv_xingbie)
    TextView mTvXingbie;
    //昵称输入框
    @BindView(R.id.act_register_et_nick)
    EditText mEtNick;
    //倒计时
    //验证码获取按钮
    @BindView(R.id.act_register_tv_yanzheng)
    TextView mTvYanzheng;
    //倒计时内部类
    private MyCount mc;
    //用户信息
    private User user;
    //密码，手机号，昵称，性别,验证码
    private String strUserPwd="",strPhoneNumber="",strUserNick="",strUserSex="",strYanZheng="";
    //用户头像相关
    private Bitmap head;  //头像Bitmap
    public static String path="/sdcard/Head/";  //sd路径
    public static File file;//头像文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        user=new User();
       initViewOpers();
    }
    protected void initViewOpers() {
        /**
         * 选择头像
         */
        mIvTouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInfo("点击头像");
                AlertDialog mAlertDialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("选择更换方式")
                        .setCancelable(false)
                        .setNegativeButton("从相册中选取", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent1, 1);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                                startActivityForResult(intent2, 2);// 采用ForResult打开
                                dialog.dismiss();
                            }
                        }).show();
                mAlertDialog=builder.create();
            }
        });
    }

    /**
     * 根据返回requestCode不同进行不同的操作
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 保存头像，并给当前注册用户设置头像
                         */
                        mIvTouxiang.setImageBitmap(head);
                        setPicToView(head);// 保存在SD卡中
                    }
                }
                break;
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统裁剪功能
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File filePath = new File(path);
        filePath.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        file=new File(fileName);
        file.mkdirs();
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void unBind() {

    }


    /**
     * 性别选择点击事件
     */
    @OnClick(R.id.act_register_tv_xingbie)
    public void onClick() {
        showShareDialog();
    }

    /**
     * 获取验证码按钮，根据输入框的手机号码发送验证码
     * @param v
     */
    public void onHuoQuClick(View v) {
        Log.i("MyTag", "获取验证码");
        Toast.makeText(this, "获取验证码", Toast.LENGTH_SHORT).show();
        strPhoneNumber=getTvContent(mEtPhone);
        if (!MyUtils.isPhone(strPhoneNumber)) {
            toShort("手机号码输入不正确！");
            return;
        }
        BmobSMS.requestSMSCode(this, strPhoneNumber, "pikeSMS", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    logInfo("短信id：" + smsId);
                    toShort("短信id：" + smsId);

                } else {
                    logInfo(e.getLocalizedMessage() + "  " + e.toString());
                    toShort(e.getLocalizedMessage());
                }
            }
        });
        mc=new MyCount(60000, 1000);
        mc.start();
    }


    public void onRegisterClick(View v){
        strPhoneNumber=getTvContent(mEtPhone);
        strUserPwd=getTvContent(mEtPwd);
        strYanZheng=getTvContent(mEtYanzheng);
        strUserNick=getTvContent(mEtNick);
        strUserSex=getTvContent(mTvXingbie);
        if(head==null){
            toShort("请选择头像");
            return;
        }
        if(strPhoneNumber.equals("")){
            toShort("手机号码不能为空");
            return;
        }
        if (!MyUtils.isPhone(strPhoneNumber)) {
            toShort("手机号码输入不正确！");
            return;
        }
        if(strUserPwd.equals("")){
            toShort("密码不能为空！");
            return;
        }
        if(strUserPwd.length()<6||strUserPwd.length()>16){
            toShort("密码为6~16位字符！");
            return;
        }
        if(strYanZheng.equals("")){
            toShort("验证码不能为空！");
            return;
        }
        if(strUserNick.equals("")){
            toShort("用户昵称不能为空！");
            return;
        }
        if(strUserSex.equals("")){
            toShort("请选择性别！");
            return;
        }

        BmobSMS.verifySmsCode(this, strPhoneNumber, strYanZheng, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                if (ex == null) {//短信验证码已验证成功
                    Log.i("MyTap", "验证通过");
                    toShort("验证通过");
                    user.setUsername(strPhoneNumber);
                    user.setPassword(strUserPwd);
                    user.setUserNick(strUserNick);
                    user.setMobilePhoneNumberVerified(true);
                    user.setMobilePhoneNumber(strPhoneNumber);
                    user.setUserHeadPortrait(new BmobFile(file));
                    user.getUserHeadPortrait().upload(new UploadFileListener() {
                        @Override
                        public void done(cn.bmob.v3.exception.BmobException e) {
                            if(e==null){//头像上传成功
                                toShort("头像上传成功");
                                user.signUp(new SaveListener<User>() {
                                    @Override
                                    public void done(User s, cn.bmob.v3.exception.BmobException e) {
                                        if (e == null) {
                                            toShort("注册成功:" + s.toString());
                                            MyApplcation.sUser=s;
//                                            RegisterActivity.this.finish();
                                        } else {
                                            logInfo(e+"");
                                            toShort("注册失败" + e);
                                        }
                                    }
                                });
                            }
                        }
                    });


                } else {
                    Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                    toShort("验证码不正确");
                }
            }
        });
    }



    /**
     * 打印一句话
     * @param s
     */
    private void logInfo(String s) {
        Log.i("MyTag",s);
    }

    /**
     * 吐司一句话显示三秒
     * @param s
     */
    private void toShort(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }


    /**
     * 倒计时的内部类
     */
    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时结束
         */
        @Override
        public void onFinish() {
            mTvYanzheng.setText("重新获取");
            mTvYanzheng.setBackgroundColor(Color.parseColor("#FF02A5F7"));
            mTvYanzheng.setTextColor(Color.WHITE);
            mTvYanzheng.setClickable(true);
        }

        /**
         * 倒计时过程
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mTvYanzheng.setText(millisUntilFinished / 1000 + "秒");
            mTvYanzheng.setBackgroundColor(Color.parseColor("#ffffff"));
            mTvYanzheng.setTextColor(Color.BLACK);
            mTvYanzheng.setClickable(false);
        }
    }


    /**
     * 自定义提示框，用来选择性别
     */
    private void showShareDialog() {
        //获取自定义提示框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.share_xingbie_view, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(this, R.style.common_dialog);
        dialog.setContentView(view);
        dialog.show();
        // 监听
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.view_share_nv://女
                        mTvXingbie.setText("女");
                        break;
                    case R.id.view_share_nan://男
                        mTvXingbie.setText("男");
                        break;
                    case R.id.share_cancel_btn://取消
                        break;
                }
                dialog.dismiss();
            }

        };
        //获取自定义提示框的控件
        ViewGroup mViewWeixin = (ViewGroup) view.findViewById(R.id.view_share_nv);
        ViewGroup mViewPengyou = (ViewGroup) view.findViewById(R.id.view_share_nan);
        Button mBtnCancel = (Button) view.findViewById(R.id.share_cancel_btn);
        //给控件设置监听事件
        mViewWeixin.setOnClickListener(listener);
        mViewPengyou.setOnClickListener(listener);
        mBtnCancel.setOnClickListener(listener);

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    /**
     * 获取控件内容
     * @param view
     * @return
     */
    public String getTvContent(TextView view){
        return view.getText().toString().trim();
    }

    /**
     * 后退按钮监听事件
     * @param v
     */
    public void onBackClick(View v){
        this.finish();
    }

}
