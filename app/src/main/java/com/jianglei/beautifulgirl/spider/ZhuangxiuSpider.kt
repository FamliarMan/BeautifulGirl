package com.jianglei.beautifulgirl.spider

import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.vo.PictureTypeVo
import org.jsoup.Jsoup

/**
 * 妆秀网爬虫
 * @author jianglei on 1/6/19.
 */
class ZhuangxiuSpider : DataSource {
    override fun fetchTitles(url: String, page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>) {
        val realUrl = url.substring(0, url.length - 2) + "$page/"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String?) {
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
                        PictureTitleVo(name, "", detailUrl, coverUrl)
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
        },"gb2312")
    }

    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        val realUrl = url.substring(0, url.length - 2) + "$page/"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String?) {
                if (html == null) {
                    listener.onError("解析网页失败")
                    return
                }
                try {

                    val doc = Jsoup.parse(html)

                    val contentPic = doc.select("div[class~=content-pic$]")
                    if (contentPic.size == 0) {
                        listener.onSuccess(ArrayList())
                        return
                    }
                    val img = contentPic[0].getElementsByTag("img")[0]
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
        },"gb2312")
    }

    override fun fetAllTypes(homePageUrl: String, listener: OnDataResultListener<MutableList<PictureTypeVo>>) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String?) {
                if (html == null) {
                    listener.onError("Network Error!")
                    return
                }
                try {
                    val res: List<PictureTypeVo>
                    val doc = Jsoup.parse(html)
                    val nav = doc.getElementsByClass("nav")[0]
                    val types = nav.getElementsByTag("li")
                    res = types.map {
                        val a = it.getElementsByTag("a")[0]
                        PictureTypeVo(a.text(), a.attr("href"))
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

        },"gb2312")
    }
}