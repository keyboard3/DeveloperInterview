package com.github.keyboard3.developerinterview.Http;

import com.allenliu.versionchecklib.core.VersionParams;
import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.UpgradService;
import com.github.keyboard3.developerinterview.entity.Version;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyboard3 on 2017/9/6.
 */

public class HttpClient {
    private final HttpService service;
    public final VersionParams.Builder hostBuilder;
    public final VersionParams.Builder selfBuilder;

    public HttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fir.im/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(HttpService.class);

        hostBuilder = new VersionParams.Builder()
                .setRequestUrl(Config.upgradHostUrl)
                .setApkName("interview")
                .setDownloadAPKPath(Config.StorageDirectory)
                .setService(UpgradService.class);
        selfBuilder = new VersionParams.Builder()
                .setApkName("selfView")
                .setOnlyDownload(true)
                .setRequestUrl(Config.upgradSelfUrl)
                .setDownloadAPKPath(Config.StorageDirectory)
                .setService(UpgradService.class);
    }

    static class Single {
        static HttpClient instance = new HttpClient();
    }

    public static HttpClient getInstance() {
        return Single.instance;
    }

    public void upgrad(String appId, String api_token, Callback<Version> versionCall) {
        Call<Version> upgrad = service.upgrad(appId, api_token);
        upgrad.enqueue(versionCall);
        upgrad.request();
    }
}
