package com.xdlteam.pike.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.Video;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yin on 2016/11/3.
 */

public class FindFragment extends Fragment {
    @BindView(R.id.fragment_find_recyclerview)
    RecyclerView mFragmentFindRecyclerview;
    private FindFragmentAdapter mAdapter;

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

        ArrayList<Video> videos = new ArrayList<>();
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());
        videos.add(new Video());

        mAdapter = new FindFragmentAdapter(new ArrayList<Video>());
        mFragmentFindRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        mFragmentFindRecyclerview.setAdapter(mAdapter);

        mAdapter.addAll(videos);
    }

}
