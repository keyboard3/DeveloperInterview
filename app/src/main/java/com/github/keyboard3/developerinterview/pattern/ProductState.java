package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.fragment.ProductListFragment;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class ProductState extends BaseProblemState {
    public static final int type = 7;
    public static final String typeStr = "product";

    static class Single {
        static ProductState instance = new ProductState();
    }

    public static ProductState getInstance() {
        return Single.instance;
    }

    @Override
    public BaseProblemState setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.INVISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ProductListFragment(), typeStr);
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_product;
    }

    @Override
    public String getTypeStr() {
        return typeStr;
    }

    @Override
    public int getType() {
        return type;
    }
}
