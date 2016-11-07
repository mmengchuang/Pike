package com.xdlteam.pike.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.nitrico.lastadapter.LastAdapter;
import com.xdlteam.pike.BR;
import com.xdlteam.pike.R;
import com.xdlteam.pike.databinding.FragmentFindItemBinding;
import com.xdlteam.pike.viewmodel.VideoModel;
import com.xdlteam.pike.bean.Video;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.jvm.functions.Function1;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by Yin on 2016/11/3.
 */

public class FindFragment extends Fragment implements LastAdapter.OnClickListener {
	public static final String TAG = FindFragment.class.getSimpleName();
	@BindView(R.id.fragment_find_recyclerview)
	RecyclerView mFragmentFindRecyclerview;/*
	private ArrayList<Video> mVideos;*/
	private CompositeSubscription mSubscription;
	private VideoModel mModel;
	private ObservableArrayList<Video> mVideos;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

		mFragmentFindRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,
				StaggeredGridLayoutManager.VERTICAL));


		mSubscription.add(
				mModel.getVideos(0)
						.subscribe(new Observer<List<Video>>() {
							@Override
							public void onCompleted() {
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
						})
		);

		LastAdapter.with(mVideos, BR.item)
				.map(Video.class, R.layout.fragment_find_item)
				.onClickListener(this)
				.into(mFragmentFindRecyclerview);
	}

	@Override
	public void onClick(@NotNull Object o, @NotNull View view, int i, int i1) {
        /*Intent intent = new Intent(getActivity(), TestActivity.class);
        Video video= (Video) o;
        intent.putExtra("VIDEO", (Parcelable) video);
        startActivity(intent);*/
	}
}
