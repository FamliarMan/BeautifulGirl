package com.jianglei.beautifulgirl.spider

import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.WebsiteDescVo
import org.jsoup.Jsoup

/**
 * 妆秀网爬虫
 * @author jianglei on 1/6/19.
 */
@WebSource(false)
class ZhuangxiuSpider : WebDataSource {

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "妆秀性感美女图片",
            "http://www.zhuangxiule.cn/",
            R.mipmap.ic_zhuangxiu,
            "图片"
        )
    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl = url.substring(0, url.length - 2) + "$page/"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    val dds = doc.getElementsByClass("list-left public-box")[0]
                        .getElementsByTag("dd")
                    val res = dds.filter {
                        it.getElementsByTag("img").size > 0
                    }.map {
                        val a = it.getElementsByTag("a")[0]
                        val detailUrl = a.attr("href")
                        val name = a.text()
                        val imgs = a.getElementsByTag("img")
                        val coverUrl = imgs[0].attr("src")
                        ContentTitle(name, "", detailUrl, coverUrl)
                    }.toMutableList()
                    listener.onSuccess(res)
                } catch (e: Exception) {
                    listener.onError(e.toString())
                }
            }

            override fun onError(code: Int, msg: String) {
                if (code == 404) {
                    listener.onSuccess(ArrayList())
                } else {
                    listener.onError(msg)
                }
            }
        }, "gb2312")
    }

    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        val realUrl = url.substring(0, url.length - 2) + "$page/"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {

                    val doc = Jsoup.parse(html)

                    val contentPic = doc.select("div[class~=content-pic$]")
                    if (contentPic.size == 0) {
                        listener.onSuccess(ArrayList())
                        return
                    }
                    val imags = contentPic[0].getElementsByTag("img")
                    if (imags.size == 0) {
                        listener.onSuccess(ArrayList())
                        return

                    }
                    val img = imags[0]
                    val res = ArrayList<String>()
                    res.add(img.attr("src"))
                    listener.onSuccess(res)
                } catch (e: Exception) {

                    listener.onError(e.toString())
                }
            }

            override fun onError(code: Int, msg: String) {
                if (code == 404) {
                    listener.onSuccess(ArrayList())
                } else {
                    listener.onError(msg)
                }
            }
        }, "gb2312")
    }

    override fun fetchAllCategory(
        homePageUrl: String,
        listener: OnDataResultListener<MutableList<Category>>,
        page: Int
    ) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val res: List<Category>
                    val doc = Jsoup.parse(html)
                    val nav = doc.getElementsByClass("nav")[0]
                    val types = nav.getElementsByTag("li")
                    res = types.map {
                        val a = it.getElementsByTag("a")[0]
                        Category(a.text(), a.attr("href"))
                    }.filter {
                        it.title != "首页"
                    }
                    listener.onSuccess(res.toMutableList())
                } catch (e: Exception) {
                    listener.onError(e.toString())
                }


            }

            override fun onError(code: Int, msg: String) {
                if (code == 404) {
                    listener.onSuccess(ArrayList())
                } else {
                    listener.onError(msg)
                }
            }

        }, "gb2312")
    }
}