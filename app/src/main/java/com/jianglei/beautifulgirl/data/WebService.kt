package com.jianglei.beautifulgirl.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

/**
 * @author jianglei on 1/4/19.
 */
interface WebService {

    /**
     * 获取某个网站html
     */
    @GET
    fun fetchHtmlFromWebsite(@Url url: String): Call<ResponseBody>

    /**
     * 获取91播放页html
     */
    @Headers("Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
    @GET
    fun fetchHtmlFromWebsite(@Url url: String,@Header("X-Forwarded-For")ip:String): Call<ResponseBody>
}