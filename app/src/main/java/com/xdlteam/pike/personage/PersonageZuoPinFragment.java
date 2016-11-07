package com.xdlteam.pike.personage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdlteam.pike.R;

/**
 * Created by Yin on 2016/11/4.
 */

public class PersonageZuoPinFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_personage_zuopin, container, false);
	}
}
