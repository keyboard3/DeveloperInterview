package com.github.keyboard3.developerinterview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    public void initViewHolder(BaseAdapter.ViewHolder viewHolder) {
        viewHolder.listener = new InnerItemViewClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        };
        viewHolder.itemView.setOnClickListener(viewHolder.listener);
    }

    //内部列表 优化的clickListener
    public abstract class InnerItemViewClickListener implements View.OnClickListener {
        public int position;
    }

    //外部listView的 itemClick
    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    //优化后itemClick的viewHolder
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
