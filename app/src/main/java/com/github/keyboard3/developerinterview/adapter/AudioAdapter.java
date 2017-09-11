package com.github.keyboard3.developerinterview.adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.github.keyboard3.developerinterview.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import nl.changer.audiowife.AudioWife;

/**
 * 语音列表页适配器
 * Created by keyboard3 on 2017/9/3.
 */

public class AudioAdapter extends BaseAdapter<AudioAdapter.MyViewHolder, String> {
    public static final String TAG = "AudioAdapter";

    public AudioAdapter(List<String> data, Activity activity) {
        super(data, activity);
    }

    @Override
    public AudioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_audio, parent, false));
        initViewHolder(viewHolder);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AudioAdapter.MyViewHolder holder, int position) {
        if (awr.get() == null) return;
        holder.setPosition(position);
        String audioUrl = data.get(position);
        //初始化默认UI
        new AudioWife().init(awr.get(), Uri.parse(audioUrl))
                .useDefaultUi(holder.audioContainer, awr.get().getLayoutInflater());
    }

    public static class MyViewHolder extends BaseAdapter.ViewHolder {
        public ViewGroup audioContainer;
        public CheckBox radioButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            audioContainer = itemView.findViewById(R.id.audio_container);
            radioButton = itemView.findViewById(R.id.rb_check);
        }
    }

    public List<String> getSelectedItems() {
        Log.d(TAG, "getSelectedItems");
        List<String> list = new ArrayList<>();
        for (ViewHolder item1 :
                viewHolders) {
            MyViewHolder item = (MyViewHolder) item1;
            if (item.radioButton.isChecked()) {
                String path = data.get(item.position);
                Log.d(TAG, "viewHolder-hash¬code:" + item.hashCode() + "position:" + item.position + " path:" + path);
                list.add(path);
            }
        }
        return list;
    }

    public void init() {
        Log.d(TAG, "init");
        for (ViewHolder item1 : viewHolders) {
            MyViewHolder item = (MyViewHolder) item1;
            if (item == null) continue;
            item.audioContainer.removeAllViews();
            if (item.radioButton.isChecked()) {
                item.radioButton.setChecked(false);
            }
        }
    }
}
