package com.github.keyboard3.developerinterview.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.adapter.BaseAdapter;
import com.github.keyboard3.developerinterview.adapter.ProductAdapter;
import com.github.keyboard3.developerinterview.base.BaseFragment;
import com.github.keyboard3.developerinterview.entity.PluginInfo;
import com.qihoo360.replugin.RePlugin;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 产品列表
 *
 * @author keyboard3
 */
public class ProductsFragment extends BaseFragment {

    @BindView(R.id.rl_content) RecyclerView productPluginsView;
    private List<PluginInfo> appPlugins = new ArrayList<>();
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this,getView());
        initAppPlugins();
        initPluginsViewWithData();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideDialog();
    }

    void initAppPlugins() {
        appPlugins.add(new PluginInfo("selfView",
                "com.github.keyboard3.selfview",
                "com.github.keyboard3.selfview.MainActivity",
                "自定义view集合"));

    }

    void initPluginsViewWithData() {
        productPluginsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProductAdapter(appPlugins, getActivity());
        productPluginsView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                PluginInfo entity = appPlugins.get(position);
                com.qihoo360.replugin.model.PluginInfo plugin = RePlugin.install(ConfigConst.STORAGE_DIRECTORY + "/" + entity.name + ".apk");
                if (plugin != null) {
                    RePlugin.preload(plugin);
                }
                showDialog();
                RePlugin.startActivity(getActivity(), RePlugin.createIntent(entity.packageName,
                        entity.mainClass));
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(itemTouchCallback);
        touchHelper.attachToRecyclerView(productPluginsView);
    }

    ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder) {
            int flag = ItemTouchHelper.LEFT;
            return makeMovementFlags(0, flag);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final PluginInfo entity = appPlugins.get(viewHolder.getAdapterPosition());
            new AlertDialog.Builder(getActivity()).setTitle(R.string.com_tint)
                    .setMessage(R.string.products_dialog_uninstall)
                    .setPositiveButton(getString(R.string.com_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (RePlugin.uninstall(entity.packageName)) {
                                Toast.makeText(getActivity(), R.string.product_uninstall_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.product_uninstall_fail, Toast.LENGTH_SHORT).show();
                            }
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton(getString(R.string.com_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    adapter.notifyDataSetChanged();
                }
            }).create().show();
        }
    };
}