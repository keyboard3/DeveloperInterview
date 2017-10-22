package com.github.keyboard3.developerinterview.http;

import com.github.keyboard3.developerinterview.entity.Version;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 网络请求接口类
 *
 * @author keyboard3
 * @date 2017/9/6
 */

public interface HttpService {
    /**
     * fir升级接口
     *
     * @param appId
     * @param sort
     * @return
     */
    @GET("/apps/latest/{appId}")
    Call<Version> upgrade(@Path("appId") String appId, @Query("api_token") String sort);
}
