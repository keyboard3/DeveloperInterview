package com.github.keyboard3.developerinterview.http;

import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.entity.Version;

import java.util.List;

import io.reactivex.Observable;
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
    @GET("http://api.fir.im/apps/latest/{appId}")
    Observable<Version> upgrade(@Path("appId") String appId, @Query("api_token") String sort);

    /**
     * 根据题目类型获取题目集合
     *
     * @param type
     * @return
     */
    @GET("master/app/src/main/assets/{problemType}")
    Observable<List<Problem>> getProblems(@Path("problemType") String type);
}
