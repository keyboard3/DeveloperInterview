package com.github.keyboard3.developerinterview.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 基类适配器
 *
 * @author keyboard3
 * @date 2017/9/3
 */

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, type> extends RecyclerView.Adapter<T> {
    protected WeakReference<Activity> weakActivity;
    protected List<ViewHolder> viewHolders;
    protected List<type> data;

    private OnItemClickListener itemClickListener;

    public BaseAdapter(@NonNull List<type> data,@NonNull Activity activity) {
        this.data = data;
        weakActivity = new WeakReference(activity);
        viewHolders = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    protected void initViewHolder(BaseAdapter.ViewHolder viewHolder) {
        //创建viewHolder的时候就绑定好点击事件、绑定数据时设置当前viewHolder的数据索引。触发点击事件时拿到当前的数据索引
        viewHolder.listener = new InnerItemViewClickListener() {

            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(view, position);
            }
        };
        viewHolder.itemView.setOnClickListener(viewHolder.listener);
    }

    public void setOnItemClickListener(@NonNull OnItemClickListener clickListener) {
        itemClickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /**
     * 内部列表 优化的clickListener
     */
    public abstract class InnerItemViewClickListener implements View.OnClickListener {
        public int position;
    }

    /**
     * 优化后itemClick的viewHolder
     */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public int position;
        public BaseAdapter.InnerItemViewClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setPosition(int position) {
            this.position = position;
            if (listener != null)
                listener.position = position;
        }
    }
}
