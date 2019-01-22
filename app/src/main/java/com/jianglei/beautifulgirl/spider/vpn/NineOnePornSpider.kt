package com.jianglei.beautifulgirl.spider.vpn

import android.util.Log
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.PlayUrl
import com.jianglei.beautifulgirl.vo.WebsiteDescVo
import org.jsoup.Jsoup
import java.lang.StringBuilder


/**
 * @author jianglei on 1/9/19.
 */
@WebSource(true, 3)
class NineOnePornSpider : WebDataSource {
    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "91自拍",
            "http://www.91porn.com/v.php?next=watch",
            R.mipmap.ic_91,
            "视频"
        )
    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl = "$url&page=$page"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    val listchannel = doc.getElementsByClass("listchannel")
                    val res = listchannel.map {

                        var imagechannel = it.getElementsByClass("imagechannel")
                        if (imagechannel == null || imagechannel.size == 0) {
                            imagechannel = it.getElementsByClass("imagechannelhd")
                        }
                        val a = imagechannel[0]
                            .getElementsByTag("a")[0]
                        //获取跳转地址url
                        val detailUrl = a.attr("href")
                        //获取封面url
                        val coverUrl = a.getElementsByTag("img")[0]
                            .attr("src")
                        val title = a.getElementsByTag("img")[0].attr("title")

                        val allInfo = it.text()
                        val sb = StringBuilder()
                        val sindex = allInfo.indexOf("时长")

                        val duration = allInfo.substring(sindex + 3, sindex + 8)
                        sb.append("时长:").append(duration).append(" ")

                        val start = allInfo.indexOf("添加时间")
                        val info = allInfo.substring(start).replace("还未被评分", "")
                        sb.append(info)
                        val desc = sb.toString()
                        val res = ContentTitle(title, desc, detailUrl, coverUrl)
                        res.type = Category.TYPE_VIDEO
                        Log.d("jianglei", res.title + "  " + res.detailUrl)
                        res

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
    }

    override fun fetchAllCategory(
        homePageUrl: String,
        listener: OnDataResultListener<MutableList<Category>>,
        page: Int
    ) {
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                try {
                    val a = doc.getElementById("navsubbar")
                        .getElementsByTag("a")
                    val res = a.map {

                        val category = Category(it.text(), it.attr("href"))
                        category.type = Category.TYPE_VIDEO
                        category
                    }.filter {
                        it.title != "高清"
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

    override fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayUrl>>) {

        RetrofitManager.get91Html(detailUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {

                try {
                    val doc = Jsoup.parse(html)

                    val videoUrl = doc.select("video").first().select("source").first().attr("src")
                    Log.d("jianglei", "视频链接：$videoUrl")
                    val playUrl = PlayUrl(true, "mp4", "360", videoUrl)
                    val res = listOf(playUrl)
                    listener.onSuccess(res.toMutableList())

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