package com.xdlteam.pike.personage;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nitrico.lastadapter.LastAdapter;
import com.squareup.picasso.Picasso;
import com.xdlteam.pike.BR;
import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.util.RxBus;
import com.xdlteam.pike.videodetails.VideoDetailsActivity;
import com.xdlteam.pike.viewmodel.UserModel;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PersonageActivity extends AppCompatActivity implements LastAdapter.OnClickListener {

	@BindView(R.id.act_personage_userimage)
	CircleImageView mActPersonageUserimage;
	@BindView(R.id.act_personage_sex_imageview)
	CircleImageView mActPersonageSexImageview;
	@BindView(R.id.act_personage_frameLayout_iv)
	TextView mActPersonageFrameLayoutIv;
	@BindView(R.id.act_personage_zuopin_count)
	TextView mActPersonageZuopinCount;
	@BindView(R.id.activity_personage_xihuan_tv)
	TextView mXihuanTv;
	@BindView(R.id.activity_personage_guanzhu_tv)
	TextView mActivityPersonageGuanzhuTv;
	@BindView(R.id.activity_personage_fensi_tv)
	TextView mActivityPersonageFensiTv;
	@BindView(R.id.linearLayout2)
	LinearLayout mLinearLayout2;
	@BindView(R.id.textView10)
	TextView mTextView10;
	@BindView(R.id.activity_personage_zhuopin)
	LinearLayout mActivityPersonageZhuopin;
	@BindView(R.id.activity_personage_xihuan)
	LinearLayout mActivityPersonageXihuan;
	@BindView(R.id.linearLayout3)
	LinearLayout mLinearLayout3;
	private User mUser;
	private UserModel mUserModel;
	@BindView(R.id.act_personage_toolbar)
	Toolbar mActPersonageToolbar;
	@BindView(R.id.act_personage_toolbarlayout)
	CollapsingToolbarLayout mActPersonageToolbarlayout;
	@BindView(R.id.act_personage_toolbar_image)
	ImageView mActPersonageToolbarImage;
	@BindView(R.id.act_personage_recyclerview)
	RecyclerView mActPersonageRecyclerview;
	private Random mRandom;
	private ObservableArrayList<Video> mVideos;
	private CompositeSubscription mSubscription;
	private Observable<List<Video>> mZhuoPingObservable;
	private final static String TAG = PersonageActivity.class.getSimpleName();

	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, PersonageActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personage);
		ButterKnife.bind(this);
		mUserModel = new UserModel();
		mUser = mUserModel.getUser();
		mVideos = new ObservableArrayList<>();

		mZhuoPingObservable=mUserModel.getVideos(mUser.getObjectId());//请求作品的流
		//设置用户拥有的视频列表不可见
		mActPersonageRecyclerview.setVisibility(View.GONE);
		mActPersonageFrameLayoutIv.setVisibility(View.GONE);

		mZhuoPingObservable
				.count()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Integer>() {
					@Override
					public void onCompleted() {
						Log.d(TAG, "用户作品完成加载");
					}

					@Override
					public void onError(Throwable throwable) {
						Snackbar.make(mActPersonageUserimage,throwable.getMessage(),Snackbar.LENGTH_SHORT);
					}

					@Override
					public void onNext(Integer integer) {
						mActPersonageZuopinCount.setText(integer+"");
					}
				});

		GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
		layoutManager.setSmoothScrollbarEnabled(true);
		layoutManager.setAutoMeasureEnabled(true);
		mActPersonageRecyclerview.setLayoutManager(layoutManager);
		mActPersonageRecyclerview.setHasFixedSize(false);
		mActPersonageRecyclerview.setNestedScrollingEnabled(false);


		LastAdapter.with(mVideos, BR.item)
				.map(Video.class, R.layout.fragment_find_item)
				.onClickListener(PersonageActivity.this)
				.into(mActPersonageRecyclerview);

		mSubscription = new CompositeSubscription();


		if (mUser != null) {
			Picasso.with(this)
					.load(mUser.getUserHeadPortrait().getFileUrl())
					.resize(72, 72)
					.centerCrop()
					.into(mActPersonageUserimage);

			//喜欢textView
			mXihuanTv.setText(MessageFormat.format("{0}", mUser.getUserShouCang().size()));
			//关注textView
			mActivityPersonageGuanzhuTv.setText(MessageFormat.format("{0}", mUser.getUserGuanZhu().size()));
			//粉丝textView
			mUserModel.getFensiCount(mUser.getObjectId())
					.subscribe(new Subscriber<Integer>() {
						@Override
						public void onCompleted() {
							Log.d(TAG, "onCompleted:设置粉丝成功");
						}

						@Override
						public void onError(Throwable throwable) {
							Log.d(TAG, "onError:" + throwable.getMessage());
						}

						@Override
						public void onNext(Integer integer) {
							mActivityPersonageFensiTv.setText(MessageFormat.format("{0}", integer));
						}
					});



			/*
			设置用户的性别
			 */
			int resid = -1;
			if (mUser.getUserSex().equals("男")) {
				resid = R.drawable.dialog_xingbie_nan;
			} else if (mUser.getUserSex().equals("女")) {
				resid = R.drawable.dialog_xingbie_nv;
			}
			Picasso.with(this)
					.load(resid)
					.resize(16, 16)
					.centerCrop()
					.into(mActPersonageSexImageview);

			/**
			 * 设置toolbar的标题
			 */
			mActPersonageToolbar.setTitle(mUser.getUserNick());
			setSupportActionBar(mActPersonageToolbar);
			ActionBar actionBar = getSupportActionBar();
			mActPersonageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onBackPressed();
				}
			});

			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}


			mSubscription.add(mUserModel.getVideos(mUser.getObjectId())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(subscriberVideo()));
		}


		mRandom = new Random();
		setImageHead();

		/*ArrayList<Video> arrayList = new ArrayList<>();
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());
		arrayList.add(new Video());


		mActPersonageRecyclerview.setNestedScrollingEnabled(false);
		mActPersonageRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,
				StaggeredGridLayoutManager.VERTICAL));
		FindFragmentAdapter fragmentAdapter = new FindFragmentAdapter(new ArrayList<Video>());
		mActPersonageRecyclerview.setAdapter(fragmentAdapter);

		fragmentAdapter.addAll(arrayList);
		mActPersonageRecyclerview.setVisibility(View.GONE);*/
	}


	private Subscriber<List<Video>> subscriberVideo() {
		return new Subscriber<List<Video>>() {
			@Override
			public void onCompleted() {
				if (mVideos.size() > 0) {
					mActPersonageRecyclerview.setVisibility(View.VISIBLE);
					mActPersonageFrameLayoutIv.setVisibility(View.GONE);
				} else {
					mActPersonageFrameLayoutIv.setVisibility(View.VISIBLE);
					mActPersonageRecyclerview.setVisibility(View.GONE);
				}
				Log.d(TAG, "onCompleted: 用户拥有的视频加载完成");
			}

			@Override
			public void onError(Throwable throwable) {
				Snackbar.make(mActPersonageUserimage,
						throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
			}

			@Override
			public void onNext(List<Video> videos) {
				mVideos.clear();
				mVideos.addAll(videos);
			}
		};
	}

	/**
	 * 给appbar里的imageview设置图片
	 */
	private void setImageHead() {
		switch (mRandom.nextInt(2)) {
			case 0:
				Picasso.with(this).load(R.drawable.ic_personage_head).into(mActPersonageToolbarImage);
				break;
			case 1:
				Picasso.with(this).load(R.drawable.ic_personage_head1).into(mActPersonageToolbarImage);
				break;
		}
	}

	@Override
	public void onClick(@NotNull Object o, @NotNull View view, int i, int i1) {
		Intent intent=new Intent(this, VideoDetailsActivity.class);
		RxBus.getDefault().post((Video)o);
		startActivity(intent);
	}

	@OnClick(value = {R.id.activity_personage_zhuopin, R.id.activity_personage_xihuan})
	public void myOnClick(View view) {
		switch (view.getId()) {
			case R.id.activity_personage_zhuopin:
				mSubscription.add(mZhuoPingObservable
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(subscriberVideo()));
				break;
			case R.id.activity_personage_xihuan:
				mSubscription.add(mUserModel.getVideos(mUser.getUserShouCang())
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(subscriberVideo()));
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSubscription.clear();
	}
}
