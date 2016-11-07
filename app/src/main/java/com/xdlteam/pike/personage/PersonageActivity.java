package com.xdlteam.pike.personage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.Video;
import com.xdlteam.pike.home.FindFragmentAdapter;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonageActivity extends AppCompatActivity {

	@BindView(R.id.act_personage_toolbar)
	Toolbar mActPersonageToolbar;
	@BindView(R.id.act_personage_toolbarlayout)
	CollapsingToolbarLayout mActPersonageToolbarlayout;
	@BindView(R.id.act_personage_toolbar_image)
	ImageView mActPersonageToolbarImage;
	@BindView(R.id.act_personage_recyclerview)
	RecyclerView mActPersonageRecyclerview;
	private Random mRandom;

	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, PersonageActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personage);
		ButterKnife.bind(this);

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
		mRandom = new Random();

		setImageHead();

		ArrayList<Video> arrayList = new ArrayList<>();
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
		mActPersonageRecyclerview.setVisibility(View.GONE);
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
