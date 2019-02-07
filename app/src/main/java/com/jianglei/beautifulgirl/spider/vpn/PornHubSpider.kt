package com.jianglei.beautifulgirl.spider.vpn

import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.net.URL
import java.util.regex.Pattern


/**
 * pornhub 的爬虫
 * @author jianglei on 1/13/19.
 */
@WebSource(true)
class PornHubSpider : WebVideoSource,SearchSource{

    private var callHolder = ArrayList<Call<PornHubKeyWordWrapper>>()
    private var playRegx = "var flashvars_[\\d]*\\s*=\\s*(\\{.+\\})"
    private var playPattern = Pattern.compile(playRegx)

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "pornhub",
            "https://www.pornhub.com/categories",
            R.mipmap.pornhub,
            "视频"
        )
    }
    override fun fetchCoverContents(activity: FragmentActivity, url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl: String?
        if (url.contains("?")) {
            realUrl = "$url&page=$page"
        } else {
            realUrl = "$url?page=$page"
        }
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                var ul = doc.select("#videoCategory")
                if (ul.size == 0) {
                    //说明当前是搜索状态
                    ul = doc.select("#videoSearchResult")
                }
                val res = ul.select("li")
                    .map {
                        val host = URI(url)
                        val wrap = it.selectFirst(".wrap")
                        val imgFade = wrap.selectFirst("[class~=img fade]")
                        val a = imgFade.selectFirst("a")
                        var detailUrl = a.attr("href")
                        detailUrl = "https://" + host.host + detailUrl
                        val title = a.attr("title")
                        val img = imgFade.selectFirst("img")
                        var coverUrl = img.attr("src")
                        if (!coverUrl.startsWith("http")) {
                            //特殊情况，src不是正常的图片地址，转而去取其他属性
                            coverUrl = img.attr("data-thumb_url")
                        }
                        val desc = it.selectFirst(".views").text()
                        val contentTitle = ContentTitle(title, desc, detailUrl, coverUrl,Category.TYPE_VIDEO)
                        contentTitle
                    }.toMutableList()

                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetchAllCategory(homePageUrl: String, listener: OnDataResultListener<MutableList<Category>>, page: Int) {
        if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    val catPics = doc.select("[class~=cat_pic]")
                    val res = catPics.map {
                        val a = it.selectFirst("a")
                        val title = a.attr("alt")
                        val img = a.selectFirst("img")
                        var coverUrl = img.attr("src")
                        if (!coverUrl.startsWith("http")) {
                            //特殊情况，src不是正常的图片地址，转而去取其他属性
                            coverUrl = img.attr("data-thumb_url")
                        }
                        val host = URL(homePageUrl)
                        val url = "https://" + host.host + a.attr("href")
                        val category = Category(title, url,Category.TYPE_VIDEO)
                        category.coverUrl = coverUrl
                        category
                    }.toMutableList()

                    //将首页加上

                    val categoryHome = Category("首页",
                        "https://www.pornhub.com/video",Category.TYPE_VIDEO)
                    categoryHome.coverUrl =
                            "https://ci.phncdn.com/videos/201811/30/194467401/original/(m=ecuKGgaaaa)(mh=R0CvAjxoPPyvzHVm)14.jpg"
                    categoryHome.type = Category.TYPE_VIDEO
                    res.add(0, categoryHome)
                    listener.onSuccess(res)
                } catch (e: Exception) {
                    listener.onError(e.localizedMessage)

                }
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayContent>>) {
        RetrofitManager.getWebsiteHtml(detailUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val matcher = playPattern.matcher(html)
                if (!matcher.find()) {
                    listener.onSuccess(ArrayList())
                    return
                }
                val playPathVoStr = matcher.group(1)
                val pathWrapper = Gson().fromJson(playPathVoStr, PathWrapper::class.java)

                val playContent = PlayContent(pathWrapper.mediaDefinitions,"","")
                listener.onSuccess(listOf(playContent) as MutableList<PlayContent>)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun getSearchSuggest(keyword: String, listener: OnDataResultListener<MutableList<SearchVideoKeyWord>>) {
        val call = RetrofitManager.retrofit
            .create(WebService::class.java)
            .pornhubSearch(keyword)
        callHolder.add(call)
        call.enqueue(object : Callback<PornHubKeyWordWrapper> {
            override fun onFailure(call: Call<PornHubKeyWordWrapper>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<PornHubKeyWordWrapper>, response: Response<PornHubKeyWordWrapper>) {
                val body = response.body()
                if (body == null) {
                    listener.onError("No suggest")
                    return
                }
                val res = body.queries.map {
                    SearchVideoKeyWord(it, "")

                }.toMutableList()
                listener.onSuccess(res)

            }
        })
    }

    override fun getSearchUrl(searchTxt: String): String {
        val text = searchTxt.replace(" ", "+")
        return "https://www.pornhub.com/video/search?search=$text"
    }

    data class PathWrapper(var mediaDefinitions: MutableList<PlayUrl>)

    data class PornHubKeyWordWrapper(var queries: MutableList<String>)

}