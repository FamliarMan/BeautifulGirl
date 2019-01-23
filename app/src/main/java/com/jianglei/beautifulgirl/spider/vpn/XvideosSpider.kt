package com.jianglei.beautifulgirl.spider.vpn

import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URL
import java.util.regex.Pattern

/**
 * xvideos的爬虫
 * @author jianglei on 1/10/19.
 */
@WebSource(true)
class XvideosSpider : WebVideoSource, SearchSource {
    /**
     * 当前获取封面内容的页码
     */
    private var titlePage = 1

    /**
     * 下一页必须要带上的数字,xvideos 首页翻页直接借助页码来进行
     * 但其他频道翻页都是利用上一页返回的html数据的首行中的一个注释中的时间戳来进行
     * 请求的，所以要获取这个时间戳才行
     */
    private var nextPageNum: String? = null

    /**
     * 获取下一个时间戳的正则
     */
    private val regx = "<!--[\\s*]([\\d]{10})[\\s*]-->"
    private val pattern = Pattern.compile(regx)

    /**
     * 获取高清MP4视频地址的正则
     */
    private val highMp4 = "setVideoUrlHigh\\('(.*?)'\\)"
    private val highMp4Pattern = Pattern.compile(highMp4)
    /**
     * 获取高清ts视频地址的正则
     */
    private val highTS = "setVideoHLS\\('(.*?)'\\)"
    private val highTSPattern = Pattern.compile(highTS)

    private val categoryRegx = "<img src=\"(.*?)\""
    private val categoryPattern = Pattern.compile(categoryRegx)
    @Transient
    private var callHolder = ArrayList<Call<XVideoKeyWordWrapper>>()


    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "xvideos",
            "https://www.xvideos.com/channels-index",
            R.mipmap.xvideos,
            "视频"
        )
    }

    /**
     * 拿到获取下一页封面内容的真正的url的值
     */
    private fun getRealUrl(partUrl: String): String? {
        //首页频道
        if (partUrl.startsWith("https://www.xvideos.com/new")) {
            if (titlePage == 1) {
                return "https://www.xvideos.com"
            }
            return "$partUrl/" + (titlePage - 1)
        }
        //搜索时
        if (partUrl.startsWith("https://www.xvideos.com/?k=")) {
            return when {
                titlePage == 1 -> partUrl
                else -> partUrl + "&p=" + (titlePage - 1)
            }

        }
        //剩下是普通频道
        return when {
            titlePage == 1 -> "$partUrl/activity"
            nextPageNum != null -> "$partUrl/activity/$nextPageNum"
            else -> null
        }

    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        titlePage = page
        val realUrl = getRealUrl(url)
        if (realUrl == null) {
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                nextPageNum = getNextPageNum(html)
                val doc = Jsoup.parse(html)
                val thumbBlock = doc.select(".thumb-block")
                val res = thumbBlock.map {
                    val a = it.selectFirst("a")
                    val host = URL(url)
                    val detailUrl = "https://" + host.host + a.attr("href")
                    val coverUrl = a.selectFirst("img").attr("data-src")
                    val thumbUnder = it.selectFirst(".thumb-under")
                    val title = thumbUnder.selectFirst("a").text()
                    val desc = thumbUnder.selectFirst(".metadata").text()
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


    override fun fetchAllCategory(
        homePageUrl: String,
        listener: OnDataResultListener<MutableList<Category>>,
        page: Int
    ) {
        val realUrl = "$homePageUrl/" + (page - 1)
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {

                    val doc = Jsoup.parse(html)

                    val thumbBlock = doc.getElementsByClass("thumb-block")
                    val res = thumbBlock.map {
                        val imgtext = it.selectFirst(".thumb")
                            .selectFirst("script").data()

                        val coverUrl = getCategroyUrl(imgtext)

                        val a = it.select(".profile-name")
                            .first()
                            .select("a").first()
                        val categoryName = a.text()
                        val partUrl = a.attr("href")
                        val url = URL(homePageUrl)
                        val detailUrl = "https://" + url.host + partUrl
                        val desc = it.selectFirst(".profile-counts")
                            .text()
                        val category = Category(categoryName, detailUrl,Category.TYPE_VIDEO)
                        category.coverUrl = coverUrl
                        category.desc = desc
                        category.type = Category.TYPE_VIDEO
                        category

                    }.toMutableList()
                    val home = Category("Home", "https://www.xvideos.com/new",Category.TYPE_VIDEO)
                    home.desc = "The newest videos"
                    home.type = Category.TYPE_VIDEO
                    //随便用一张图
                    home.coverUrl =
                            "https://img-egc.xvideos-cdn.com/videos/thumbs169ll/2e/f7/27/2ef727330799ad95781ea178195040b6/2ef727330799ad95781ea178195040b6.29.jpg"
                    res.add(0, home)
                    listener.onSuccess(res)
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener.onError(e.localizedMessage)
                }
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }


    override fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayUrl>>) {
        RetrofitManager.getWebsiteHtml(detailUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val realPlayUrl = getRealPlayUrl(html)
                if (realPlayUrl == null) {
                    listener.onError("获取播放地址失败")
                    return
                }

                val playUrl = PlayUrl(true, "tls", "720", realPlayUrl)
                val res = listOf(playUrl)
                listener.onSuccess(res.toMutableList())

            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    private fun getNextPageNum(html: String): String? {
        val lines = html.split("\n")
        if (lines.isEmpty()) {
            return null
        }
        val matcher = pattern.matcher(lines[0])
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    private fun getRealPlayUrl(html: String): String? {
        val highMp4Matcher = highMp4Pattern.matcher(html)
        if (highMp4Matcher.find()) {
            return highMp4Matcher.group(1)
        }
        val highTsMatcher = highTSPattern.matcher(html)
        if (highTsMatcher.find()) {
            return highTsMatcher.group(1)
        }
        return null
    }

    private fun getCategroyUrl(text: String): String? {
        val m = categoryPattern.matcher(text)
        return if (m.find()) {
            m.group(1)
        } else {
            null
        }


    }

    override fun getSearchUrl(searchTxt: String): String {
        return "https://www.xvideos.com/?k=$searchTxt&top"
    }

    override fun cancelAllNet() {
        super.cancelAllNet()
        callHolder.forEach {
            it.cancel()
        }
    }

    override fun getSearchSuggest(keyword: String, listener: OnDataResultListener<MutableList<SearchVideoKeyWord>>) {
        cancelAllNet()
        val suggestCall = RetrofitManager.retrofit
            .create(WebService::class.java)
            .xvideoSearch(keyword)

        callHolder.add(suggestCall)
        suggestCall.enqueue(object : Callback<XVideoKeyWordWrapper> {
            override fun onFailure(call: Call<XVideoKeyWordWrapper>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<XVideoKeyWordWrapper>,
                response: Response<XVideoKeyWordWrapper>
            ) {
                val res = response.body()
                if (res != null) {
                    listener.onSuccess(res.KEYWORDS)
                }
            }
        })

    }

}