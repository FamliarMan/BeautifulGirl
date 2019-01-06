package com.jianglei.beautifulgirl.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * @author jianglei on 1/4/19.
 */
interface WebService {

    /**
     * 饭粒动态图的养眼美女栏目[page]是当前页码，从0开始
     *
     */
    @GET("https://www.retuwo.com/meinv/page/{page}")
    fun fetchFanli(@Path("page") page: Int): Call<ResponseBody>

    /**
     * 获取某个帖子的所有图片
     */
    @GET
    fun fetchDetailList(@Url url: String): Call<ResponseBody>

}