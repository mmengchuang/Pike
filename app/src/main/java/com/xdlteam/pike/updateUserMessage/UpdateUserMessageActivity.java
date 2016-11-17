package com.xdlteam.pike.updateUserMessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdlteam.pike.R;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UpdateUserMessageActivity extends AppCompatActivity {

    private EditText mEtNike;
    private TextView mTvSex,mTvZhangHao,mTvPersonJieShao;
    private ImageView mIvHeadPic;//头像
    private TextView mTvUpdateHeadPic;
    private Bitmap head;  //头像Bitmap
    public static String path="/sdcard/";  //sd路径
    public static File file=new File(path,"Head.jpg");//头像文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_message);
        initView();
        initDatas();
        initOpers();
    }

    private void initView() {
        mEtNike= (EditText) findViewById(R.id.update_user_message_nike);
        mTvUpdateHeadPic= (TextView) findViewById(R.id.update_user_message_head_tv);
        mIvHeadPic= (ImageView) findViewById(R.id.update_user_message_head_iv);
        mTvSex= (TextView) findViewById(R.id.update_user_message_sex);
        mTvZhangHao= (TextView) findViewById(R.id.update_user_message_zhanghao);
        mTvPersonJieShao= (TextView) findViewById(R.id.update_user_message_person_jieshao);
    }

    private void initDatas() {
        if(file.exists()){
            Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
            mIvHeadPic.setImageBitmap(bitmap);//设置头像
        }else{
            BmobFile userHeadPortrait = MyApplcation.sUser.getUserHeadPortrait();
            String headJPGName = userHeadPortrait.getFileUrl().substring(userHeadPortrait.getFileUrl().length() - 32 - 4, userHeadPortrait.getFileUrl().length() - 4);
            Log.i("myTag",userHeadPortrait.getFilename().toString());
            Log.i("myTag",headJPGName);
            final File file=new File("/sdcard/"+headJPGName+".jpg");
            if(file.exists()){
                mIvHeadPic.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }else{
                userHeadPortrait.download(new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        mIvHeadPic.setImageBitmap(BitmapFactory.decodeFile(s));
                        Bitmap bitmap = BitmapFactory.decodeFile(s);
                        if(bitmap==null){
                            return;
                        }
                        try {
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                });
            }

        }

        mTvZhangHao.setText(MyApplcation.sUser.getUsername());//设置用户账号
        mTvSex.setText(MyApplcation.sUser.getUserSex());//设置性别
        mEtNike.setText(MyApplcation.sUser.getUserNick());//设置昵称

    }

    private void initOpers() {
        mTvUpdateHeadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateUserMessageActivity.this,"点击头像",Toast.LENGTH_SHORT);
                //创建警告对话框
                AlertDialog mAlertDialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(UpdateUserMessageActivity.this);
                //设置对话框内容
                builder.setTitle("选择更换方式")
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
//                    if (head != null) {
                        /**
                         * 保存头像，并给当前注册用户设置头像
                         */
//                        mIvHeadPic.setImageBitmap(head);
//                        setPicToView(head);// 保存在SD卡中
//                    }
                    setPicToView(head);
                    final User user=new User();
                    user.setUserHeadPortrait(new BmobFile(file));
                    user.getUserHeadPortrait().upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e!=null){
                                Toast.makeText(UpdateUserMessageActivity.this,"头像上传失败",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(UpdateUserMessageActivity.this,"头像上传成功",Toast.LENGTH_SHORT).show();
                            user.update(MyApplcation.sUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e!=null){
                                        Toast.makeText(UpdateUserMessageActivity.this,"更新用户头像失败",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(UpdateUserMessageActivity.this,"更新用户头像成功",Toast.LENGTH_SHORT).show();
                                    mIvHeadPic.setImageBitmap(head);
                                }
                            });
                        }

                    });

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
        intent.putExtra("aspectX", 0.8);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 80);
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
        String fileName = path + "Head.jpg";// 图片名字
        file=new File(fileName);
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

    public void selectSexClick(View v){
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
                        mTvSex.setText("女");

                        break;
                    case R.id.view_share_nan://男
                        mTvSex.setText("男");
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

    public void updateClick(View v){
        String nike=mEtNike.getText().toString().trim();
        String sex = mTvSex.getText().toString().trim();
        User user=new User();
        user.setUserSex(sex);
        user.setUserNick(nike);

        user.update(MyApplcation.sUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Toast.makeText(UpdateUserMessageActivity.this,"信息更新成功",Toast.LENGTH_SHORT).show();
            }
        });

    }



}
