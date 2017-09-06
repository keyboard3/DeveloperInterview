package com.github.keyboard3.developerinterview.Http;

import com.allenliu.versionchecklib.core.VersionParams;
import com.github.keyboard3.developerinterview.UpgradService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyboard3 on 2017/9/6.
 */

public class HttpClient {
    private final HttpService service;
    public final VersionParams.Builder builder;

    public HttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fir.im/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(HttpService.class);

        builder = new VersionParams.Builder()
                .setRequestUrl("http://api.fir.im/apps/latest/59acbc2d959d6940060002ee?api_token=aefd90754f9288ff2e54949a8bc21e20")
                .setService(UpgradService.class);
    }

    static class Single {
        static HttpClient instance = new HttpClient();
    }

    public static HttpClient getInstance() {
        return Single.instance;
    }

}
