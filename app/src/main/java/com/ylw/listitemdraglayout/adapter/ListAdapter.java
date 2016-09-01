package com.ylw.listitemdraglayout.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ylw.listitemdraglayout.R;

/**
 * Created by 袁立位 on 2016/8/5 16:56.
 */
public class ListAdapter extends BaseAdapter {

    public ListAdapter(Context context) {
        this.context = context;
    }

    String[] datas = new String[]{};
    private Context context;

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int i) {
        return datas[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.simple_list_item, null);
            holder = new ItemHolder(view);
            view.setTag(holder);
        } else {
            holder = (ItemHolder) view.getTag();
        }

        String s = datas[i];
        holder.name.setText(s);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"KKKKKKKKKKK",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    class ItemHolder {
        TextView name;

        public ItemHolder(View view) {
            name = (TextView) view.findViewById(R.id.right_item);
        }
    }
}
