package com.xdlteam.pike.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.xdlteam.pike.R;
import com.xdlteam.pike.camera.CameraActivity;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HomePresenter mHomePresenter;
    @BindView(R.id.activity_home_dl)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_home_nv)
    NavigationView mNavigationView;
    @BindView(R.id.activity_home_tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.activity_home_vp)
    ViewPager mViewPager;
    @BindView(R.id.activity_home_toolbar)
    Toolbar mToolbar;
    @BindArray(R.array.home_activity_tabstitle)
    String[] mTabsTitles;
    private ActionBarDrawerToggle drawerToggle;
    private Fragment[] mFragments = new Fragment[3];

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        //navigationview
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar
                , R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        //初始化viewpager的fragment
        mFragments[0] = new OneFragment();
        mFragments[1] = new FindFragment();
        mFragments[2] = new SanFragment();

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabsTitles[position];
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        //设置初始为第二个
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    //NavigationItemSelected的选择事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.Recording:
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
