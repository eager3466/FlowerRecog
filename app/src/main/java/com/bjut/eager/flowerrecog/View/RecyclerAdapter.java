package com.bjut.eager.flowerrecog.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjut.eager.flowerrecog.Bean.Item;
import com.bjut.eager.flowerrecog.R;

import java.util.ArrayList;

/**
 * Created by Eager on 2016/9/27.
 * Used for showing identified result by ListView.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private ArrayList<Item> mItems = null;
    Context mContext;

    public RecyclerAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mItems = items;
    }


    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.desc.setText("描述："+ mItems.get(position).getDescription());
        float prob = mItems.get(position).getProbability();
        prob =  (float)(Math.round(prob*1000000))/10000;
        holder.prob.setText("概率：" + prob + "%");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView desc;
        public TextView prob;
        public ViewHolder(View view){
            super(view);
            desc = (TextView)view.findViewById(R.id.description);
            prob = (TextView)view.findViewById(R.id.probability);
        }
    }
}
