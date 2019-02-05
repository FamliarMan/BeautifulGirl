package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.vpn.PornHubSpider
import com.jianglei.beautifulgirl.vo.XVideoKeyWordWrapper
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
    fun fetchHtmlFromWebsite(@Url url: String,@HeaderMap header:Map<String,String>): Call<ResponseBody>


    @GET("https://www.xvideos.com/search-suggest/{keyword}")
    fun xvideoSearch(@Path("keyword")keyWord: String):Call<XVideoKeyWordWrapper>

    @GET("https://www.pornhub.com/video/search_autocomplete?pornstars=true")
    fun pornhubSearch(@Query("q")keyWord: String):Call<PornHubSpider.PornHubKeyWordWrapper>
}