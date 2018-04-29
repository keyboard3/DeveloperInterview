package com.github.keyboard3.developerinterview.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.entity.Problem;

import java.util.List;

/**
 * 问题列表页适配器
 *
 * @author keyboard3
 * @date 2017/9/3
 */
public class ProblemAdapter extends BaseAdapter<ProblemAdapter.MyViewHolder, Problem> {


    public ProblemAdapter(List<Problem> data, Activity activity) {
        super(data, activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_problem, parent, false));
        initViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setPosition(position);
        Problem entity = data.get(position);
        holder.icon.setImageResource(entity.getTypeIcon());
        holder.tvTitle.setText(entity.title);
        holder.tvContent.setText(entity.content);
    }

    public static class MyViewHolder extends BaseAdapter.ViewHolder {
        public ImageView icon;
        public TextView tvTitle;
        public TextView tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_type);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
