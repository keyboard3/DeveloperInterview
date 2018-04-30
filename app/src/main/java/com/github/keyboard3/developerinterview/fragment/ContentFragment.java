package com.github.keyboard3.developerinterview.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.base.BaseFragment;
import com.github.keyboard3.developerinterview.view.ExtProgressWebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 面经基本内容
 *
 * @author keyboard3
 */
public class ContentFragment extends BaseFragment {
    @BindView(R.id.wb_content) WebView htmlContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this,getView());
        initHtmlWithFixContent();
    }

    private void initHtmlWithFixContent() {
        String content = "[Android 名企面试题及涉及知识点整理。](https://github.com/karmalove/AndroidInterview)\n" +
                "[gtibook版面试资料](http://www.jackywang.tech/AndroidInterview-Q-A/)\n" +
                "[［干货］2017已来，最全面试总结——这些Android面试题你一定需要](http://www.jianshu.com/p/39e8768843d2)\n" +
                "[Android工程师之Android面试大纲](http://url.cn/5UwS1I8)\n" +
                "[\\[干货，阅后进BAT不是梦\\]面试心得与总结---BAT、网易、蘑菇街](http://mp.weixin.qq.com/s?__biz=MzI0MjE3OTYwMg==&mid=2649547962&idx=1&sn=85a1fc344644a3d6af9c46dae485a24c&scene=21#wechat_redirect)\n" +
                "[面经福利］如何进入百度、阿里，一个6年Android老司机的面经](http://mp.weixin.qq.com/s?__biz=MzI0MjE3OTYwMg==&mid=2649548208&idx=1&sn=2ff22db04700e16ad4764eafc7e72e0d&chksm=f1180ecdc66f87dbbc78a8a9d6c450a0d4ba93843bb9ccee69c9a574e50ec9e7cae83b83e415&scene=21#wechat_redirect)\n" +
                "[Android 面试题&&面经（收集）](http://www.jianshu.com/p/13786463635d)\n" +
                "[Android面试题集合](https://zhuanlan.zhihu.com/p/25671699)\n" +
                "[Android面试专栏](https://zhuanlan.zhihu.com/androidinterview)\n" +
                "[2017已来，最全面试总结——这些Android面试题你一定需要](http://www.androidchina.net/6474.html)\n" +
                "[Android面试题整理](https://ydmmocoo.github.io/2016/06/22/Android%E9%9D%A2%E8%AF%95%E9%A2%98%E6%95%B4%E7%90%86/)\n" +
                "[40 个 Android 面试题](https://juejin.im/entry/57aad0728ac247005f4cfa81)\n" +
                "[写在Android面试回来后【可能最全的Android面试总结】](http://www.imooc.com/article/11965)\n" +
                "[Android 面试指南](https://github.com/stormzhang/android-interview-questions-cn)\n" +
                "[Java / Android 笔试、面试 知识整理](https://github.com/hadyang/interview)\n" +
                " [Android面试攻略](http://blog.csdn.net/xinanheishao/article/details/74857986)";
        String regex = "\\s*\\[(.+?)\\]\\((.+?)\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        StringBuilder htmlData = new StringBuilder();
        while (matcher.find()) {
            htmlData.append("<a href='").append(matcher.group(2)).append("'>").append(matcher.group(1)).append("</a></br>");
        }
        htmlContainer.getSettings().setDefaultTextEncodingName("UTF-8");
        htmlContainer.loadData(htmlData.toString(), "text/html; charset=UTF-8", null);
        htmlContainer.getSettings().supportZoom();
        htmlContainer.getSettings().setJavaScriptEnabled(true);
        htmlContainer.setWebViewClient(new ExtProgressWebViewClient(this));
    }

    public boolean canGoBack(){
        if (htmlContainer == null) return false;
        return htmlContainer.canGoBack();
    }

    public void goBack(){
        if(htmlContainer == null) return;
        htmlContainer.goBack();
    }
}
