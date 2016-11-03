package com.xdlteam.pike.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yin on 2016/11/3.
 */

public class FindFragmentAdapter extends RecyclerView.Adapter<FindFragmentAdapter.FindViewHolder> {
    private List<Video> mVideos;
    private static Random sRandom;

    public FindFragmentAdapter(ArrayList<Video> videos) {
        mVideos = videos;
        sRandom = new Random();
    }

    public void addAll(List<Video> videos) {
        mVideos.addAll(videos);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mVideos.clear();
        notifyDataSetChanged();
    }

    @Override
    public FindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (getType()) {
            case 0:
                view = inflater.inflate(R.layout.fragment_find_item, parent, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_find_item1, parent, false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_find_item2, parent, false);
                break;
        }

        return new FindViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.onBind(video);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    private int getType() {
        return sRandom.nextInt(2);
    }


    public static class FindViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.find_item_video_image)
        ImageView video_image;//视频的缩影
        @BindView(R.id.find_item_user_icon)
        ImageView user_icon;//发布视频的用户的图像
        @BindView(R.id.find_item_heart)
        ImageView heart;//是否已经点赞的心
        @BindView(R.id.find_item_loveCount)
        TextView loveCount;//点赞的个数

        public FindViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(Video video) {

        }
    }
}
