package com.xdlteam.pike.usermessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xdlteam.pike.R;
import com.xdlteam.pike.bean.Video;

import java.util.List;

/**
 * Created by Administrator on 2016/11/4.
 */

public class UserMessageGvAdapter extends BaseAdapter {


    private Context context;
    private List<Video> datas;
    private LayoutInflater inflater;

    public UserMessageGvAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }
    public void addAll(List<Video> datas){
        this.datas=datas;
        this.notifyDataSetChanged();
    }
    public void clearAll(){
        this.datas.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view==null){
            vh=new ViewHolder();
            view=inflater.inflate(R.layout.usermessage_gv_item,null);
            vh.iv= (ImageView) view.findViewById(R.id.activity_user_message_gv_item_iv);
            view.setTag(vh);
        }else {
            vh= (ViewHolder) view.getTag();
        }

        return view;
    }
    class ViewHolder{
        ImageView iv;
    }
}
