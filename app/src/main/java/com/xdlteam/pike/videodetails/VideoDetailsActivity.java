package com.xdlteam.pike.videodetails;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xdlteam.pike.R;
import com.xdlteam.pike.application.MyApplcation;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.contract.IVideoContract;
import com.xdlteam.pike.util.RxBus;
import com.xdlteam.pike.video.VideoPlayPresenterImpl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;
import io.vov.vitamio.widget.VideoView;
import rx.Subscription;
import rx.functions.Action1;

public class VideoDetailsActivity extends Activity implements IVideoContract.IVideoView{

    @BindView(R.id.activity_video_details_iv_back)
    ImageView mIvBack;
    @BindView(R.id.activity_video_details_iv_add)
    ImageView mIvAdd;
    @BindView(R.id.activity_video_details_iv_xin)
    ImageView mIvXin;
    @BindView(R.id.activity_video_details_iv_fenxiang)
    ImageView mIvFenxiang;
    @BindView(R.id.activity_video_details_iv_touxiang)
    CircleImageView mIvTouxiang;
    @BindView(R.id.activity_video_details_tv_usernick)
    TextView mTvUserNick;
    @BindView(R.id.activity_video_details_tv_like)
    TextView mTvLike;
    @BindView(R.id.activity_video_details_tv_pinglun)
    TextView mTvPinglun;
    @BindView(R.id.activity_video_details_lv)
    ListView mLv;
    @BindView(R.id.surface_view)
    VideoView mSurfaceView;
    private Subscription mSubscription;

    private Video mVideo;

    private User mVideoUser;

    private VideoPlayPresenterImpl mVideoPresenter;


    //微信APP_ID
    private static final String APP_ID = "wx2352b826ff56e8c5";
    //IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        regToWx();
        ButterKnife.bind(this);
        mVideoPresenter = new VideoPlayPresenterImpl(this);
        mVideoPresenter.initData();
        initDatas();
        initViewOpers();
    }

    private void initDatas() {
        mSubscription = RxBus.getDefault()
                .toObservable(Video.class)
                .subscribe(new Action1<Video>() {
                    @Override
                    public void call(Video video) {
                        mVideo = video;
//                        Toast.makeText(VideoDetailsActivity.this, mVideo.getObjectId(), Toast.LENGTH_SHORT).show();
                    }
                });
        Log.i("MyTag",mVideo.getUserId()+"userid");

        //播放视频
        mVideoPresenter.playfunction(mVideo.getVideo_content().getUrl());

        BmobQuery<User> query=new BmobQuery<>();
        query.getObject(mVideo.getUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    mVideoUser=user;
                    Log.i("MyTag","查询成功");
                    mTvUserNick.setText(mVideoUser.getUserNick());
                    Picasso.with(VideoDetailsActivity.this).load(mVideoUser.getUserHeadPortrait().getFileUrl()).into(mIvTouxiang);
                }else {
                    Log.i("MyTag",e.getLocalizedMessage());
                }
            }
        });
        mTvLike.setText(mVideo.getLoveCount()+"");
        if(MyApplcation.sUser.getUserGuanZhu().contains(mVideo.getUserId())){
            mIvAdd.setVisibility(View.GONE);
        }else {
            mIvAdd.setVisibility(View.VISIBLE);
        }

        if(MyApplcation.sUser.getUserShouCang().contains(mVideo.getObjectId())){
            mIvXin.setImageResource(R.drawable.act_videodetails_xinhong);
        }else {
            mIvXin.setImageResource(R.drawable.act_videodetails_xinhei);
        }

    }

    /**
     * 事件监听
     */
    private void initViewOpers() {
        /**
         * 关注按钮点击事件
         */
        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIvAdd.setVisibility(View.GONE);
                HolderFollow hf=new HolderFollow();
                hf.userId=MyApplcation.sUser.getObjectId();
                hf.videoUserId=mVideo.getUserId();
                userFollow(hf);
            }
        });
        /**
         * 收藏按钮点击事件
         */
        mIvXin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HolderCollection hc=new HolderCollection();
                hc.user=MyApplcation.sUser;
                hc.video=mVideo;
                if(MyApplcation.sUser.getUserShouCang().contains(mVideo.getObjectId())){
                    mIvXin.setImageResource(R.drawable.act_videodetails_xinhei);
                    hc.flag=false;
                }else {
                    mIvXin.setImageResource(R.drawable.act_videodetails_xinhong);
                    hc.flag=true;
                }
                userCollection(hc);
            }
        });
        /**
         * 分享按钮点击事件
         */
        mIvFenxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareDialog();
            }
        });

    }


    /**
     * 自定义提示框，用来选择分享位置
     */
    private void showShareDialog() {
        //获取自定义提示框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.share_fenxiang_view, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(this, R.style.common_dialog);
        dialog.setContentView(view);
        dialog.show();
        // 监听
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.view_share_haoyou://微信好友
                        Toast.makeText(VideoDetailsActivity.this, "微信好友", Toast.LENGTH_SHORT).show();
                        shareWX("测试分享", true);
                        break;
                    case R.id.view_share_pengyouquan://微信朋友圈
                        Toast.makeText(VideoDetailsActivity.this, "微信朋友圈", Toast.LENGTH_SHORT).show();
                        shareWX("测试分享", false);
                        break;
                    case R.id.view_share_qqhaoyou://QQ好友
                        Toast.makeText(VideoDetailsActivity.this, "QQ好友", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share_fenxiang_btn://取消
                        break;
                }
                dialog.dismiss();
            }

        };
        //获取自定义提示框的控件
        ViewGroup mViewWeixinHaoYou = (ViewGroup) view.findViewById(R.id.view_share_haoyou);
        ViewGroup mViewPengyouQuan = (ViewGroup) view.findViewById(R.id.view_share_pengyouquan);
        ViewGroup mViewQQHaoYou = (ViewGroup) view.findViewById(R.id.view_share_qqhaoyou);
        Button mBtnCancel = (Button) view.findViewById(R.id.share_fenxiang_btn);
        //给控件设置监听事件
        mViewQQHaoYou.setOnClickListener(listener);
        mViewWeixinHaoYou.setOnClickListener(listener);
        mViewPengyouQuan.setOnClickListener(listener);
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
     * 微信分享
     */

    protected void shareWX(String str, boolean flag) {
        Log.i("MyTag", "分享方法");
        WXTextObject textObject = new WXTextObject();
        textObject.text = str;
        //用WXTextObject
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = str;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.currentTimeMillis() + "";
        req.message = msg;
        if (flag) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        api.sendReq(req);
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = "http://www.baidu.com";
//
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = "WebPage Title";
//        msg.description = "WebPage Description";
//
//        Bitmap thumb= BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_user_image_test);
//        msg.thumbData=Util.bmpToByteArray(thumb,true);
//
//        SendMessageToWX.Req req=new SendMessageToWX.Req();
//        req.transaction=System.currentTimeMillis()+"";
//        req.message=msg;
//        req.scene=SendMessageToWX.Req.WXSceneTimeline;
//
//        api.sendReq(req);
    }

    @Override
    public VideoView getVideoView() {
        return mSurfaceView;
    }

    /**
     * 关注相关内部类
     */
    class HolderFollow {
        String userId;
        String videoUserId;
    }

    /**
     * 将当前视频发布人ID加入当前用户表关注字段
     *
     * @param holder
     */
    protected void userFollow(HolderFollow holder) {
        User user = new User();
        ArrayList<String> als = MyApplcation.sUser.getUserGuanZhu();
        als.add(holder.videoUserId);
        MyApplcation.sUser.setUserGuanZhu(als);
        user.setUserGuanZhu(als);
        user.update(holder.userId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("MyTag", "关注成功");
                    Toast.makeText(VideoDetailsActivity.this, "关注成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("MyTag", "关注失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 收藏相关内部类
     */
    class HolderCollection {
        User user;
        Video video;
        boolean flag;
    }

    /**
     * 根据用户需求进行收藏和取消收藏操作，flag为true，进行收藏操作，flag为false，进行取消收藏操作<br/>
     * 收藏，将当前视频ID加入当前用户表中<br/>
     * 取消收藏，将当前视频ID从当前用户表中删除
     *
     * @param holder
     */
    protected void userCollection(HolderCollection holder) {

        if (holder.flag) {//收藏操作
            /**
             * 将当前视频ID添加到当前用户表中
             */
            User user = new User();
            ArrayList<String> als = MyApplcation.sUser.getUserShouCang();
            als.add(holder.video.getObjectId());
            MyApplcation.sUser.setUserShouCang(als);
            user.setUserShouCang(als);
            user.update(holder.user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("MyTag", "收藏成功");
                        Toast.makeText(VideoDetailsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MyTag", "收藏失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });

            /**
             * 将当前视频收藏人数加一
             */
            int count = holder.video.getLoveCount() + 1;
            holder.video.setLoveCount(count);
            Video video = new Video();
            video.setLoveCount(count);
            mTvLike.setText(count+"");
            video.update(holder.video.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("MyTag", "数量增加成功");
//                        Toast.makeText(VideoDetailsActivity.this,"取消收藏成功！",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MyTag", "增加失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        } else {//取消收藏操作
            /**
             * 将当前视频ID从当前用户表中移除
             */
            User user = new User();
            ArrayList<String> als = MyApplcation.sUser.getUserShouCang();
            als.remove(holder.video.getObjectId());
            MyApplcation.sUser.setUserShouCang(als);
            user.setUserShouCang(als);
            user.update(holder.user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("MyTag", "取消收藏成功");
                        Toast.makeText(VideoDetailsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MyTag", "收藏失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
            /**
             * 将当前视频收藏人数减一
             */
            int count = holder.video.getLoveCount() - 1;
            holder.video.setLoveCount(count);
            Video video = new Video();
            video.setLoveCount(count);
            mTvLike.setText(count+"");
            video.update(holder.video.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("MyTag", "数量减少成功");
//                        Toast.makeText(VideoDetailsActivity.this,"取消收藏成功！",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MyTag", "减少失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}




















