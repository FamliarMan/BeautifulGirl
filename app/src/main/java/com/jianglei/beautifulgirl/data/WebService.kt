package com.jianglei.beautifulgirl.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
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

}