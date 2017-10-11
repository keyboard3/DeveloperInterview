package com.github.keyboard3.developerinterview.http;

import com.github.keyboard3.developerinterview.entity.Version;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by keyboard3 on 2017/9/6.
 */

public interface HttpService {
    @GET("/apps/latest/{appId}")
    Call<Version> upgrad(@Path("appId") String appId, @Query("api_token") String sort);
}
