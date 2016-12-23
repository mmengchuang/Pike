package com.xdlteam.pike.home;

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

import com.github.nitrico.lastadapter.LastAdapter;
import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.User;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.viewmodel.UserModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 同城
 * Created by Yin on 2016/11/3.
 */

public class CityFragment extends Fragment  {
	private static final String TAG = "CityFragment";
	@BindView(R.id.fragment_san_rv)
	RecyclerView mFragmentSanRv;
	@BindView(R.id.fragment_san_srl)
	SwipeRefreshLayout mFragmentSanSrl;
	private UserModel mUserModel;
	private User mUser;
	private ObservableArrayList<Video> mVideos;
	private CompositeSubscription mSubscription;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mUserModel = new UserModel();
		mUser = mUserModel.getUser();
		mVideos = new ObservableArrayList<>();
		mSubscription = new CompositeSubscription();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_san, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mFragmentSanRv.setVisibility(View.GONE);
		mFragmentSanSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getVideos();
			}
		});
		mFragmentSanSrl.setRefreshing(true);
		mFragmentSanRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
		LastAdapter.with(mVideos, 1)
				.map(Video.class, R.layout.item_tongcheng)
//				.onClickListener(this)
				.into(mFragmentSanRv);

		getVideos();
	}

	private void getVideos() {
		mSubscription.add(mUserModel.getAllVideos()
				.subscribe(new Subscriber<List<Video>>() {
					@Override
					public void onCompleted() {
						mFragmentSanSrl.setRefreshing(false);
						mFragmentSanRv.setVisibility(View.VISIBLE);
						Log.d(TAG, "onCompleted:视频加载成功");
					}

					@Override
					public void onError(Throwable throwable) {
						Log.d(TAG, "onError:" + throwable.getMessage());
					}

					@Override
					public void onNext(List<Video> videos) {
						mVideos.clear();
						mVideos.addAll(videos);
					}
				})
		);
	}

//	@Override
//	public void onClick(Object o, View view, int i, int i1) {
//		Intent intent=new Intent(getContext(), VideoDetailsActivity.class);
//		RxBus.getDefault().post((Video)o);
//		startActivity(intent);
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSubscription.clear();
	}
}
