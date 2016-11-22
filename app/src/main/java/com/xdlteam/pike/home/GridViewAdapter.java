package com.xdlteam.pike.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xdlteam.pike.R;
import com.xdlteam.pike.util.VideoUtils;

import java.util.List;

/**
 * Created by 11655 on 2016/11/22.
 */

public class GridViewAdapter extends BaseAdapter {
    //数据源
    private List<String> fileNames;
    private LayoutInflater inflater;
    private Context context;

    public GridViewAdapter(Context context, List<String> fileNames) {
        this.fileNames = fileNames;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileNames.size();
    }

    @Override
    public Object getItem(int i) {
        return fileNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_local_works_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.act_local_works_item_iv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
       //获取视频第一帧图片
         Bitmap bitmap = VideoUtils.createVideoThumbnail(fileNames.get(i));
        //显示图片
        viewHolder.imageView.setImageBitmap(bitmap);
        return view;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
