package com.jianglei.beautifulgirl.spider

import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import org.jsoup.Jsoup

/**
 * 94套图
 * @author jianglei on 1/18/19.
 */
class NineFourPicture : DataSource {
    override fun fetchTitles(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
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
                        val contentTitle = ContentTitle(title, "", detailUrl, coverUrl)
                        contentTitle.type = Category.TYPE_PICTURE
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

    override fun fetAllTypes(homePageUrl: String, listener: OnDataResultListener<MutableList<Category>>, page: Int) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {

                    val doc = Jsoup.parse(html)
                    val res = doc.select("#menu-nav")
                        .select("li")
                        .map {
                            it.select("a")
                        }.map {
                            val category = Category(it.text(), it.attr("href"))
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
                    val a = doc.selectFirst(".m-list-content")
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