package com.github.keyboard3.developerinterview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;

import java.util.List;

/**
 * 问题列表页适配器
 * Created by keyboard3 on 2017/9/3.
 */

public class ProblemAdapter extends BaseAdapter<ProblemAdapter.MyViewHolder> {
    List<Problem> data;

    public ProblemAdapter(List<Problem> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_problem, parent, false));
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

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class MyViewHolder extends BaseAdapter.ViewHolder {
        public ImageView icon;
        public TextView tvTitle, tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_type);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
