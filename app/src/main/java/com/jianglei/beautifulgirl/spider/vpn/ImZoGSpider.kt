package com.jianglei.beautifulgirl.spider.vpn


import androidx.fragment.app.FragmentActivity
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
class ImZoGSpider : WebVideoSource, SearchSource {


    private var dynamicWebGetter: DynamicWebGetter = DynamicWebGetter()
    private val callHolder = ArrayList<Call<*>>()
    private var isClicked = false


    override fun fetchVideoUrls(
        activity: FragmentActivity,
        detailUrl: String,
        listener: OnDataResultListener<MutableList<PlayContent>>
    ) {

    }

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "iMZoG",
            "https://imzog.com/",
            R.mipmap.imzog,
            "视频"
        )
    }

    override fun fetchCoverContents(
        activity: FragmentActivity,
        url: String,
        page: Int,
        listener: OnDataResultListener<MutableList<ContentTitle>>
    ) {
        val realUrl: String
        realUrl = if (page == 1) {
            url
        } else {
            "$url$page/"
        }
        dynamicWebGetter.getWebHtml(activity, realUrl,  object : OnWebViewResultListener {
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
                        val img = a.selectFirst("img")
                        var partUrl = img.attr("data-src")
                        if (partUrl == null || partUrl.isEmpty()) {
                            //搜索页面会出现
                            partUrl = img.attr("src")
                        }
                        val coverUrl = "https:$partUrl"
                        val contentTitle = ContentTitle(title, "", targetUrl, coverUrl, Category.TYPE_VIDEO)
                        contentTitle.isUseWeb = true
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
        call.enqueue(object : Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (!response.isSuccessful) {
                    if (response.errorBody() != null) {
                        listener.onError(response.errorBody()!!.string())
                    } else {
                        listener.onError("搜索失败")
                    }
                    return
                }
                if (response.body() == null) {
                    listener.onError("搜索失败")
                    return

                }
                val res = response.body()!!.map {
                    SearchVideoKeyWord(it, "")
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
        dynamicWebGetter.cancel()
    }


    interface ImZogService {
        @GET("https://imzog.com/suggester/suggest.php")
        fun getSearchSuggest(@Query("char") char: String): Call<List<String>>
    }

}