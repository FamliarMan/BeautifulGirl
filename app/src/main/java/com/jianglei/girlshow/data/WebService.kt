package com.jianglei.girlshow.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author jianglei on 1/4/19.
 */
interface WebService {

    /**
     * 获取某个网站html
     */
    @GET
    fun fetchHtmlFromWebsite(@Url url: String): Call<ResponseBody>

    @GET
    fun fetchHtmlFromWebsite(@Url url: String, @HeaderMap header: Map<String, String>): Call<ResponseBody>



    @GET
    fun searchSuggest(@Url url: String ): Call<ResponseBody>
}