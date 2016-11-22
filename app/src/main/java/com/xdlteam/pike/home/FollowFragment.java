package com.xdlteam.pike.home;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.nitrico.lastadapter.LastAdapter;
import com.xdlteam.pike.BR;
import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.util.RxBus;
import com.xdlteam.pike.videodetails.VideoDetailsActivity;
import com.xdlteam.pike.viewmodel.UserModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 关注页面
 * Created by Yin on 2016/11/3.
 */

public class FollowFragment extends Fragment implements LastAdapter.OnClickListener {
	private static final String TAG = "FollowFragment";
	@BindView(R.id.fragment_guanzhu_linearlayout)
	LinearLayout mFragmentGuanzhuLinearlayout;
	@BindView(R.id.fragment_guanzhu_rv)
	RecyclerView mFragmentGuanzhuRv;
	@BindView(R.id.fragment_guanzhu_srl)
	SwipeRefreshLayout mFragmentGuanzhuSrl;
	private UserModel mUserModel;
	private User mUser;
	private ObservableArrayList<Video> mViedos;
	private CompositeSubscription mSubscription;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mUserModel = new UserModel();
		mUser = mUserModel.getUser();
		mViedos = new ObservableArrayList<>();
		mSubscription = new CompositeSubscription();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guanzhu, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mFragmentGuanzhuLinearlayout.setVisibility(View.GONE);
		mFragmentGuanzhuRv.setVisibility(View.GONE);
		mFragmentGuanzhuRv.setLayoutManager(new GridLayoutManager(getContext(), 2));

		mFragmentGuanzhuSrl.setRefreshing(true);

		mFragmentGuanzhuSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				setVideos();
			}
		});

		LastAdapter.with(mViedos, BR.item)
				.map(Video.class, R.layout.fragment_find_item)
				.onClickListener(this)
				.into(mFragmentGuanzhuRv);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setVideos();
	}

	private void setVideos() {
		mSubscription.add(mUserModel.getGuanZhu(mUser.getUserGuanZhu())
				.subscribe(new Subscriber<List<Video>>() {
					@Override
					public void onCompleted() {
						Log.d(TAG, "onCompleted:" + "视频加载成功");
						if (mViedos.size() > 0) {
							mFragmentGuanzhuRv.setVisibility(View.VISIBLE);
							mFragmentGuanzhuLinearlayout.setVisibility(View.GONE);
						} else {
							mFragmentGuanzhuLinearlayout.setVisibility(View.VISIBLE);
							mFragmentGuanzhuRv.setVisibility(View.GONE);
						}
						mFragmentGuanzhuSrl.setRefreshing(false);
					}

					@Override
					public void onError(Throwable throwable) {
						Log.d(TAG, "onError:" + throwable.getMessage());
					}

					@Override
					public void onNext(List<Video> videos) {
						mViedos.clear();
						mViedos.addAll(videos);
					}
				})
		);
	}

	@Override
	public void onClick(Object o, View view, int i, int i1) {
		Intent intent=new Intent(getContext(), VideoDetailsActivity.class);
		RxBus.getDefault().post((Video)o);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSubscription.clear();
	}

}
