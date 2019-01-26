package com.jianglei.beautifulgirl.spider

import android.net.Uri
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.data.WebVideoSource
import com.jianglei.beautifulgirl.vo.*
import org.jsoup.Jsoup
import utils.UrlUtils
import java.util.regex.Pattern

/**
 * 色眯眯视频网站
 * @author jianglei on 1/24/19.
 */

@WebSource(false)
class SeMiMiSpider : WebVideoSource {
    override fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayContent>>) {
        RetrofitManager.getWebsiteHtml(detailUrl,object:OnWebResultListener{
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val script = doc.selectFirst(".player")
                    .selectFirst(".main")
                    .selectFirst("script")
                    .data()
                val url = getPlayUrl(script)
                //这里一般都只有一个视频，title和desc都不会展示，随便赋值即可
                val playContent = PlayContent(listOf(PlayUrl(true,"hls","480",url)) as MutableList<PlayUrl>,
                    "","")
                listener.onSuccess(listOf(playContent) as MutableList<PlayContent>)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "色咪咪",
            "http://www.semm97.com/",
            R.drawable.ic_launcher_foreground,
            "视频"
        )
    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        if (url.equals("http://www.semm97.com/") && page > 1) {
            //首页没有翻页
            listener.onSuccess(ArrayList())
            return
        }
        val realUrl = url.replace("pg-1", "pg-$page")
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val webHost = UrlUtils.getWebHost(url)
                val doc = Jsoup.parse(html)
                val elements =  doc.select("li[class~=p[\\d]\\sm1]")
                if(elements == null){
                    listener.onSuccess(ArrayList())
                }
                val res = elements
                    .map {

                        val a = it.selectFirst("a")
                        val title = a.attr("title")
                        val detailUrl = webHost + a.attr("href")
                        val coverUrl = webHost + a.selectFirst("img")
                            .attr("data-original")
                        ContentTitle(title, "", detailUrl, coverUrl, Category.TYPE_VIDEO)
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
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res = doc.selectFirst(".top-nav")
                    .select("li")
                    .map {
                        val a = it.selectFirst("a")
                        Category(
                            a.text(),
                            UrlUtils.getWebHost(homePageUrl) + a.attr("href"),
                            Category.TYPE_VIDEO
                        )
                    }.toMutableList()
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }


    private fun getPlayUrl(script:String):String{
        val p = Pattern.compile("unescape\\('(.*)'\\)")
        val m = p.matcher(script)
        if(m.find()){
            val playUrl = m.group(1)
            return Uri.decode(playUrl)
        }
        return ""
    }

}