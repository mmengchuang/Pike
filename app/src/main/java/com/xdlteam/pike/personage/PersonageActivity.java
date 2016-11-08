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

import com.github.nitrico.lastadapter.LastAdapter;
import com.squareup.picasso.Picasso;
import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.viewmodel.UserModel;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class PersonageActivity extends AppCompatActivity {

	@BindView(R.id.act_personage_userimage)
	CircleImageView mActPersonageUserimage;
	@BindView(R.id.act_personage_sex_imageview)
	CircleImageView mActPersonageSexImageview;
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
	private final static String TAG=PersonageActivity.class.getSimpleName();

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

		//设置用户拥有的视频列表不可见
		mActPersonageRecyclerview.setVisibility(View.GONE);
		mActPersonageRecyclerview.setLayoutManager(new GridLayoutManager(this,3));

		mSubscription = new CompositeSubscription();


		mActPersonageToolbar.setTitle("伊尹");
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

		if (mUser != null) {
			Picasso.with(this)
					.load(mUser.getUserHeadPortrait().getFileUrl())
					.resize(72, 72)
					.centerCrop()
					.into(mActPersonageUserimage);

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

			mVideos = new ObservableArrayList<>();

			mSubscription.add(mUserModel.getVideos(0, mUser.getObjectId())
					.subscribe(new Subscriber<List<Video>>() {
						@Override
						public void onCompleted() {
							Log.d(TAG, "onCompleted: 用户拥有的视频加载完成");
						}

						@Override
						public void onError(Throwable throwable) {
							Snackbar.make(mActPersonageUserimage,
									throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
						}

						@Override
						public void onNext(List<Video> videos) {
							mVideos.addAll(videos);
						}
					}));

			if (mVideos.size() > 0) {
				mActPersonageRecyclerview.setVisibility(View.VISIBLE);
			}
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

}
