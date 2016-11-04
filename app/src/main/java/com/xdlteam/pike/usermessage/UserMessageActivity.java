package com.xdlteam.pike.usermessage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.xdlteam.pike.R;
import com.xdlteam.pike.base.BaseActivity;

import static com.xdlteam.pike.R.id.activity_user_message_tv_xihuan;
import static com.xdlteam.pike.R.id.activity_user_message_tv_yinsi;
import static com.xdlteam.pike.R.id.activity_user_message_tv_zuopin;

public class UserMessageActivity extends BaseActivity {

    //查询类别文字
    private TextView tvs[]=new TextView[3];
    //TextView的id
    private int tvIds[]={activity_user_message_tv_zuopin,activity_user_message_tv_yinsi,activity_user_message_tv_xihuan};
    //gv
    private GridView gridView;
    //gv适配器
    private UserMessageGvAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        initViews();
        initDatas();
    }



    private void initViews() {
        adapter=new UserMessageGvAdapter(this);
        for (int i=0;i<tvs.length;i++){
            tvs[i]= (TextView) findViewById(tvIds[i]);
        }
        gridView= (GridView) findViewById(R.id.activity_user_message_gv);
    }
    private void initDatas() {
        gridView.setAdapter(adapter);
    }

    /**
     * 分类查询点击事件，按照类别查询当前用户的作品，隐私，喜欢
     * @param v
     */
    public void onSelectClick(View v){
        switch (v.getId()){
            case activity_user_message_tv_zuopin://查询当前用户作品
                updateTabView(0);
                break;
            case activity_user_message_tv_yinsi://查询当前用户隐私
                updateTabView(1);
                break;
            case activity_user_message_tv_xihuan://查询当前用户喜欢
                updateTabView(2);
                break;
        }
    }

    /**
     * 更新选项卡视图
     *
     * @param position 用户当前选中的位置（0-2）
     */
    private void updateTabView(int position) {
        for (int i = 0; i < 3; i++) {
            if (i == position) {
                tvs[i].setTextColor(Color.parseColor("#ff9d00"));
            } else {
                tvs[i].setTextColor(Color.parseColor("#818181"));
            }
        }
    }








    @Override
    protected void unBind() {

    }
}
