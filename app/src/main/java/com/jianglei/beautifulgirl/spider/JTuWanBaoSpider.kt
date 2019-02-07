package com.jianglei.beautifulgirl.spider

import androidx.fragment.app.FragmentActivity
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.data.WebPictureSource
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.WebsiteDescVo
import org.jsoup.Jsoup
import utils.UrlUtils

/**
 *
 * @author jianglei on 1/27/19.
 */
@WebSource(true)
class JTuWanBaoSpider : WebPictureSource {
    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        }
        val realUrl = url.substring(url.lastIndexOf("http"), url.lastIndexOf("&"))
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val partUrl = url.substring(url.lastIndexOf("http"), url.lastIndexOf("/") + 1)
                val doc = Jsoup.parse(html)
                val res = doc.select(".th_gall")
                    .filter {
                        it.selectFirst("a") != null
                    }
                    .map {
                        partUrl + it.selectFirst("a").attr("href")
                    }.toMutableList()
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })

    }

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "Jpgravure",
            "http://jpgravure.com",
            R.mipmap.web_japan_pic,
            "图片"
        )
    }

    override fun fetchCoverContents(
        activity: FragmentActivity,
        url: String,
        page: Int,
        listener: OnDataResultListener<MutableList<ContentTitle>>
    ) {
        if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        }
        try {
            RetrofitManager.getWebsiteHtml(url, object : OnWebResultListener {
                override fun onSuccess(html: String) {
                    val doc = Jsoup.parse(html)
                    val host = UrlUtils.getWebHost(url)
                    val res = doc.selectFirst(".thumbs")
                        .select(".th")
                        .map {
                            val detailUrl = host + it.attr("href")
                            val img = it.selectFirst("img")
                            val coverUrl = host + img.attr("src")
                            val title = img.attr("alt")
                            val desc = it.selectFirst("b").text()
                            ContentTitle(
                                title, desc, detailUrl, coverUrl,
                                Category.TYPE_PICTURE
                            )
                        }.toMutableList()
                    listener.onSuccess(res)
                }

                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onError(e.localizedMessage)
        }


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
                val res = doc.getElementsByClass("drop drop_cat")[0]
                    .select("li")
                    .map {
                        val a = it.selectFirst("a")
                        val title = a.text()
                        val url = host + a.attr("href")
                        Category(title, url, Category.TYPE_PICTURE)
                    }.toMutableList()
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }
}