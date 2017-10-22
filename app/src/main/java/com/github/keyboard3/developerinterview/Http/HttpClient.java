package com.github.keyboard3.developerinterview.http;

import com.allenliu.versionchecklib.core.VersionParams;
import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.UpgradService;
import com.github.keyboard3.developerinterview.entity.Version;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的外观类
 *
 * @author keyboard3
 * @date 2017/9/6
 */

public class HttpClient {
    private final HttpService mService;
    public final VersionParams.Builder mHostBuilder;
    public final VersionParams.Builder mSelfBuilder;

    public HttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fir.im/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(HttpService.class);

        mHostBuilder = new VersionParams.Builder()
                .setRequestUrl(ConfigConst.UPGRAD_HOST_URL)
                .setApkName("interview")
                .setDownloadAPKPath(ConfigConst.STORAGE_DIRECTORY)
                .setService(UpgradService.class);
        mSelfBuilder = new VersionParams.Builder()
                .setApkName("selfView")
                .setOnlyDownload(true)
                .setRequestUrl(ConfigConst.UPGRAD_SELF_URL)
                .setDownloadAPKPath(ConfigConst.STORAGE_DIRECTORY)
                .setService(UpgradService.class);
    }

    static class Single {
        static HttpClient instance = new HttpClient();
    }

    public static HttpClient getInstance() {
        return Single.instance;
    }

    public void upgrade(String appId, String api_token, Callback<Version> versionCall) {
        Call<Version> call = mService.upgrade(appId, api_token);
        call.enqueue(versionCall);
        call.request();
    }
}
