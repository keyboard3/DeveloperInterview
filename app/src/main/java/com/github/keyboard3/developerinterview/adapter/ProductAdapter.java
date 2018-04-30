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

import butterknife.BindView;
import butterknife.ButterKnife;

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
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapter.MyViewHolder holder, int position) {
        PluginInfo entity = data.get(position);
        holder.icon.setImageResource(R.mipmap.ic_menu_product);
        holder.tvTitle.setText(entity.name);
        holder.tvContent.setText(entity.desc);
    }


    public static class MyViewHolder extends BaseAdapter.ViewHolder {
        @BindView(R.id.iv_type) public ImageView icon;
        @BindView(R.id.tv_title) public TextView tvTitle;
        @BindView(R.id.tv_content) public TextView tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
