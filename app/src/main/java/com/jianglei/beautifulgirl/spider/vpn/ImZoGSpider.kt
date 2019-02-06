package com.jianglei.beautifulgirl.spider.vpn

import android.util.Log
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import utils.UrlUtils

/**
 * @author jianglei on 2/4/19.
 */
@WebSource(true)
class ImZoGSpider : WebVideoSource,SearchSource {

    companion object {

        private var callHolder = ArrayList<Call<*>>()

        /**
         * 这个网站的播放地址url是加密过的，下面是解密算法
         * [url] 是加密后的url
         */
        private fun decodeUrl(url: String): String {
            val secret = "АВСDЕFGHIJKLМNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,~"
            val videoUrl = StringBuilder()
            var k = 0
            do {

                var t1 = secret.indexOf(url[k++])
                var t2 = secret.indexOf(url[k++])
                val t3 = secret.indexOf(url[k++])
                val t4 = secret.indexOf(url[k++])
                t1 = (t1 shl 2) or (t2 shr 4)
                t2 = (t2 and 15) shl 4 or (t3 shr 2)
                val t5 = (t3 and 3) shl 6 or t4

                videoUrl.append(fromCharCode(t1))
                if (t3 != 64) {
                    videoUrl.append(fromCharCode(t2))
                }
                if (t4 != 64) {
                    videoUrl.append(fromCharCode(t5))
                }

            } while (k < url.length)
            System.out.println(videoUrl)
            return videoUrl.toString()
        }

        private fun fromCharCode(i: Int): String {
            return "" + i.toChar()
        }
    }


    override fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayContent>>) {
        DynamicWebGetter.getWebHtml(
            detailUrl, object : Condition<String> {
                override fun isValid(t: String): Boolean {
                    return t.contains("<video")
                }
            },
            object : OnWebResultListener {
                override fun onSuccess(html: String) {
                    Log.d("longyi", html)
                    listener.onError("no web")
                }

                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }
            }
        )

    }

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "iMZoG",
            "https://imzog.com/",
            R.mipmap.imzog,
            "视频"
        )
    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl: String
        if (page == 1) {
            realUrl = url
        } else {
            realUrl = "$url$page/"
        }
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res = doc.select(".lithumbnail")
                    .filter {
                        val a = it.selectFirst("a")
                        a.selectFirst("img") != null
                    }
                    .map {
                        val a = it.selectFirst("a")
                        val targetUrl = a.attr("href")
                        val title = a.attr("title")
                        val coverUrl = "https:" + a.selectFirst("img")
                            .attr("data-src")
                        val contentTitle = ContentTitle(title, "", targetUrl, coverUrl, Category.TYPE_VIDEO)
//                        contentTitle.isUseWeb = true
                        contentTitle
                    }.toMutableList()
                listener.onSuccess(res)

            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetchAllCategory(
        homePageUrl: String,
        listener: OnDataResultListener<MutableList<Category>>,
        page: Int
    ) {
        if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val host = UrlUtils.getWebHost(homePageUrl)
                val res = doc.select(".category-item")
                    .filter {
                        //广告栏目有script脚本
                        it.selectFirst("script") == null
                    }.map {
                        val a = it.selectFirst("a")
                        val title = a.attr("title")
                        val url = host + a.attr("href")
                        val coverUrl = "https:" + a.selectFirst("img")
                            .attr("src")
                        val category = Category(title, url, Category.TYPE_VIDEO)
                        category.coverUrl = coverUrl

                        category
                    }.toMutableList()
                res.sortBy { it.title }
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun getSearchSuggest(keyword: String, listener: OnDataResultListener<MutableList<SearchVideoKeyWord>>) {
        val call = RetrofitManager.retrofit.create(ImZogService::class.java)
            .getSearchSuggest(keyword)
        callHolder.add(call)
        call.enqueue(object: Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if(!response.isSuccessful){
                    if(response.errorBody() != null){
                        listener.onError(response.errorBody()!!.string())
                    }else{
                        listener.onError("搜索失败")
                    }
                    return
                }
                if(response.body() == null){
                    listener.onError("搜索失败")
                    return

                }
                val res = response.body()!!.map {
                    SearchVideoKeyWord(it,"")
                }.toMutableList()
                listener.onSuccess(res)
            }
        })
    }
    override fun getSearchUrl(searchTxt: String): String {
        return "https://imzog.com/en/search/$searchTxt/"
    }

    override fun cancelAllNet() {
        super.cancelAllNet()
        callHolder.forEach {
            it.cancel()
        }
    }


    interface ImZogService {
        @GET("https://imzog.com/suggester/suggest.php")
        fun getSearchSuggest(@Query("char") char: String): Call<List<String>>
    }

}