package com.jianglei.beautifulgirl.spider.vpn

import android.util.Log
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.Category
import org.jsoup.Jsoup
import java.net.URL


/**
 * @author jianglei on 1/6/19.
 */
@WebSource(1)
class CaoliuSpider : DataSource {
    override fun fetchTitles(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl = "$url&page=$page"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    var trs = doc.getElementsByClass("tr3 t_one tac")
                    //目前置顶帖去不掉
                    val res = trs.map {
                        val td = it.getElementsByClass("tal")[0]
                        val desc = td.text().trim()
                        val a = td.select("h3")[0].select("a")[0]
                        val fullUrl = URL(url)
                        val needUrl = "https://" + fullUrl.host + "/" + a.attr("href")
                        val title = a.text()
                        ContentTitle(title, desc, needUrl, "")
                    }.toMutableList()
                    listener.onSuccess(res)
                } catch (e: Exception) {
                    listener.onError(e.toString())
                }

            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        }, "gb2312")


    }

    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        if (page == 2) {
            //草榴图帖没有翻页
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(url, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val inputs = doc.getElementsByTag("input")
                val res = inputs.map {
                    it.attr("data-src")
                }.filter {
                    it != ""
                }.toMutableList()
                listener.onSuccess(res)
            }


            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetAllTypes(homePageUrl: String, listener: OnDataResultListener<MutableList<Category>>,page:Int) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val trs = doc.getElementsByClass("tr3 f_one")
                val res = trs.map {
                    val a = it.select("h2 > a")[0]
                    //这里取到的是部分url
                    val partUrl = a.attr("href")
                    val realUrl = homePageUrl.substring(
                        0,
                        homePageUrl.length - "index.php".length
                    ) + partUrl
                    Category(a.text(), realUrl)
                }.filter {
                    Log.d("jianglei", it.title + "  " + it.url)
                    (it.title == "新時代的我們" || it.title == "達蓋爾的旗幟")

                }.toMutableList()
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        }, "gb2312")
    }

}