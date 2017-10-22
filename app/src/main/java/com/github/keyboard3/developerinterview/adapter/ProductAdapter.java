package com.github.keyboard3.developerinterview.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.entity.PluginInfo;

import java.util.List;

/**
 * 作品适配器
 *
 * @author keyboard3
 * @date 2017/9/12
 */
public class ProductAdapter extends BaseAdapter<ProductAdapter.MyViewHolder, PluginInfo> {

    public ProductAdapter(List<PluginInfo> data, Activity activity) {
        super(data, activity);
    }

    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductAdapter.MyViewHolder viewHolder = new ProductAdapter.MyViewHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_list_problem, parent, false));
        initViewHolder(viewHolder);
        mViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapter.MyViewHolder holder, int position) {
        PluginInfo entity = mData.get(position);
        holder.icon.setImageResource(R.mipmap.ic_menu_product);
        holder.tvTitle.setText(entity.name);
        holder.tvContent.setText(entity.desc);
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
