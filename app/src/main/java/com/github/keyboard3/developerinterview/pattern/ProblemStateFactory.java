package com.github.keyboard3.developerinterview.pattern;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.SparseArray;

import com.github.keyboard3.developerinterview.R;

import java.util.HashMap;
import java.util.Map;


/**
 * 问题的状态模式和工厂模式
 * Created by keyboard3 on 2017/9/7.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ProblemStateFactory {
    public static SparseArray<? super BaseProblemState> mapType = new SparseArray<>();//限定通配符的下界
    public static Map<String, ? super BaseProblemState> mapString = new ArrayMap<>();//限定通配符的下界
    public static SparseArray<? super BaseProblemState> mapMenuId = new SparseArray<>();//限定通配符的下界

    static {
        mapType.put(JavaState.type, JavaState.getInstance());
        mapType.put(AndroidState.type, AndroidState.getInstance());
        mapType.put(HtmlState.type, HtmlState.getInstance());
        mapType.put(AlgorithmState.type, AlgorithmState.getInstance());
        mapType.put(OtherState.type, OtherState.getInstance());
        mapType.put(ProductState.type, ProductState.getInstance());

        mapString.put(JavaState.typeStr, JavaState.getInstance());
        mapString.put(AndroidState.typeStr, AndroidState.getInstance());
        mapString.put(HtmlState.typeStr, HtmlState.getInstance());
        mapString.put(AlgorithmState.typeStr, AlgorithmState.getInstance());
        mapString.put(OtherState.typeStr, OtherState.getInstance());
        mapString.put(ProductState.typeStr, ProductState.getInstance());

        mapMenuId.put(R.id.menu_java, JavaState.getInstance());
        mapMenuId.put(R.id.menu_android, AndroidState.getInstance());
        mapMenuId.put(R.id.menu_html, HtmlState.getInstance());
        mapMenuId.put(R.id.menu_algorithm, AlgorithmState.getInstance());
        mapMenuId.put(R.id.menu_other, OtherState.getInstance());
        mapMenuId.put(R.id.menu_product, ProductState.getInstance());
    }

    public static BaseProblemState getProblemType(int type) {
        return (BaseProblemState) mapType.get(type);
    }

    public static BaseProblemState getProblemType(String type) {
        return (BaseProblemState) mapString.get(type);
    }

    public static BaseProblemState getProblemTypeByMenu(int menu) {
        return (BaseProblemState) mapMenuId.get(menu);
    }
}
