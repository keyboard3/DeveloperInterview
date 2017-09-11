package com.github.keyboard3.developerinterview.pattern;

import com.github.keyboard3.developerinterview.R;

import java.util.HashMap;
import java.util.Map;


/**
 * 问题的状态模式和工厂模式
 * Created by keyboard3 on 2017/9/7.
 */

public class ProblemTypeFactory {
    public static Map<Integer, ? super ProblemType> mapType = new HashMap<>();//限定通配符的下界
    public static Map<String, ? super ProblemType> mapString = new HashMap<>();//限定通配符的下界
    public static Map<Integer, ? super ProblemType> mapMenuId = new HashMap<>();//限定通配符的下界

    static {
        mapType.put(JavaType.type, JavaType.getInstance());
        mapType.put(AndroidType.type, AndroidType.getInstance());
        mapType.put(HtmlType.type, HtmlType.getInstance());
        mapType.put(AlgorithmType.type, AlgorithmType.getInstance());
        mapType.put(OtherType.type, OtherType.getInstance());
        mapType.put(ProductType.type, ProductType.getInstance());

        mapString.put(JavaType.typeStr, JavaType.getInstance());
        mapString.put(AndroidType.typeStr, AndroidType.getInstance());
        mapString.put(HtmlType.typeStr, HtmlType.getInstance());
        mapString.put(AlgorithmType.typeStr, AlgorithmType.getInstance());
        mapString.put(OtherType.typeStr, OtherType.getInstance());
        mapString.put(ProductType.typeStr, ProductType.getInstance());

        mapMenuId.put(R.id.menu_java, JavaType.getInstance());
        mapMenuId.put(R.id.menu_android, AndroidType.getInstance());
        mapMenuId.put(R.id.menu_html, HtmlType.getInstance());
        mapMenuId.put(R.id.menu_algorithm, AlgorithmType.getInstance());
        mapMenuId.put(R.id.menu_other, OtherType.getInstance());
        mapMenuId.put(R.id.menu_product, ProductType.getInstance());
    }

    public static ProblemType getProblemType(int type) {
        return (ProblemType) mapType.get(type);
    }

    public static ProblemType getProblemType(String type) {
        return (ProblemType) mapString.get(type);
    }

    public static ProblemType getProblemTypeByMenu(int menu) {
        return (ProblemType) mapMenuId.get(menu);
    }
}
