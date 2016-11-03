package com.xdlteam.pike.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdlteam.pike.R;

/**
 * Created by 11655 on 2016/11/3.
 */

public class PhotosFragment extends BaseFragment {
    @Override
    protected void lazyLoad() {

    }

    @Override
    public View initLayout(LayoutInflater inflater, ViewGroup container, boolean b) {
        return inflater.inflate(R.layout.frag_photos,null);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {

    }
}
