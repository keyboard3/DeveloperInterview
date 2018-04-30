package com.github.keyboard3.developerinterview.http;

import android.content.Context;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.entity.Version;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的外观类
 *
 * @author keyboard3
 * @date 2017/9/6
 */

public class HttpClient {
    private static final String TAG = "HttpClient";
    private Context context;
    private HttpService mService;
    private String baseUrl = "https://raw.githubusercontent.com/keyboard3/DeveloperInterview/";
    private static HttpClient instance;
    private Consumer<Throwable> consumerError;

    public HttpClient(Context appContext) {
        this.context = appContext;
        init();
    }

    private void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(HttpService.class);
        consumerError = throwable -> {
            throwable.printStackTrace();
            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        };
    }

    public static HttpClient getInstance(Context appContext) {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if ((instance == null)) {
                    instance = new HttpClient(appContext);
                }
            }
        }
        return instance;
    }

    /**
     * 更改地址 容易让fork的项目
     *
     * @param baseUrl
     */
    public void changeBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        init();
    }

    public void upgrade(String appId, String api_token, Consumer<Version> success) {
        mService.upgrade(appId, api_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success, consumerError);
    }

    public void getProblems(String problemType, Consumer<List<Problem>> success,Consumer<Throwable> consumerError) {
        mService.getProblems(problemType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success,consumerError);
    }
}
