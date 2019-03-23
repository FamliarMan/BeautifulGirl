package com.jianglei.beautifulgirl.spider

import androidx.fragment.app.FragmentActivity
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.WebsiteDescVo
import org.jsoup.Jsoup

/**
 * 94套图
 * @author jianglei on 1/18/19.
 */
@WebSource(false)
class NineFourPicture : WebPictureSource{
    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "94套图网",
            "http://www.94xxx.pw/",
            R.mipmap.ninefour,
            "图片"
        )
    }
    override fun fetchCoverContents(activity: FragmentActivity, url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl = url + "page/$page"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    val ul = doc.selectFirst(".m-list-main")
                        .selectFirst(".f-cb")
                    val li = ul.select("li")
                    val res = li.map {
                        val img = it.selectFirst(".u-img")
                            .selectFirst("img")
                        val coverUrl = img.attr("src")
                        val title = img.attr("title")
                        val detailUrl = it.selectFirst(".u-img")
                            .selectFirst("a")
                            .attr("href")
                        val contentTitle = ContentTitle(title, "", detailUrl, coverUrl,Category.TYPE_PICTURE)
                        contentTitle
                    }.toMutableList()
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

    override fun fetchAllCategory(homePageUrl: String, listener: OnDataResultListener<MutableList<Category>>, page: Int) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {

                    val doc = Jsoup.parse(html)
                    val res = doc.select("#menu-nav")
                        .select("li")
                        .map {
                            it.select("a")
                        }.map {
                            val category = Category(it.text(),
                                it.attr("href"),Category.TYPE_PICTURE)
                            category.type = Category.TYPE_PICTURE
                            category
                        }.toMutableList()

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

    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        if (page > 1) {
            //没有翻页，直接返回空数据
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(url, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                try {
                    val a = doc.selectFirst(".m-list-contentRule")
                        .select("a")
                    val res = a.map {
                        it.attr("href")
                    }.toMutableList()
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

}