package com.xdlteam.pike.home;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.viewmodel.VideoModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Yin on 2016/11/3.
 */

public class FindFragment extends Fragment  {
	public static final String TAG = FindFragment.class.getSimpleName();
	@BindView(R.id.fragment_find_recyclerview)
	RecyclerView mFragmentFindRecyclerview;/*
  private ArrayList<Video> mVideos;*/
	@BindView(R.id.fragent_find_swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	private CompositeSubscription mSubscription;
	private VideoModel mModel;
	private ObservableArrayList<Video> mVideos;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_find, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mSubscription = new CompositeSubscription();
		mModel = new VideoModel();
		mVideos = new ObservableArrayList<>();
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
		mFragmentFindRecyclerview.setLayoutManager(gridLayoutManager);

		mSwipeRefreshLayout.setRefreshing(true);
		mFragmentFindRecyclerview.setVisibility(View.GONE);
		setVideos();

		LastAdapter.with(mVideos, 2)
				.map(Video.class, R.layout.fragment_find_item)
//				.onClickListener(this)

				.into(mFragmentFindRecyclerview);

		mFragmentFindRecyclerview.addOnScrollListener(
				new EndlessRecyclerViewScrollListener(gridLayoutManager) {
					@Override
					public void onLoadMore(int page, int totalItemsCount, final RecyclerView view) {
						mSubscription.add(
								mModel.getVideos(totalItemsCount).subscribe(new Subscriber<List<Video>>() {
									@Override
									public void onCompleted() {

									}

									@Override
									public void onError(Throwable throwable) {
										Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
									}

									@Override
									public void onNext(List<Video> videos) {
										mVideos.addAll(videos);
									}
								}));
					}
				});

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mVideos.clear();
				setVideos();
			}
		});
	}

	private void setVideos() {
		mSubscription.add(mModel.getVideos(0)
				.subscribe(new Observer<List<Video>>() {
					@Override
					public void onCompleted() {
						mSwipeRefreshLayout.setRefreshing(false);
						mFragmentFindRecyclerview.setVisibility(View.VISIBLE);
						Log.d(TAG, "onCompleted: 视频加载完成");
					}

					@Override
					public void onError(Throwable throwable) {
						Snackbar.make(mFragmentFindRecyclerview, "视频不存在", Snackbar.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(List<Video> videos) {
						mVideos.addAll(videos);
					}
				}));
	}

//	@Override
//	public void onClick(@NotNull Object o, @NotNull View view, int i, int i1) {
//		Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
//		RxBus.getDefault().post((Video) o);
//		startActivity(intent);
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSubscription != null && !mSubscription.isUnsubscribed()) {
			mSubscription.clear();
		}
	}
}
