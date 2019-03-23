package com.jianglei.beautifulgirl.spider

import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.jianglei.annotation.WebSource
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.*
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * @author jianglei on 1/25/19.
 */
@WebSource(true)
class XiuRenSpider : WebPictureSource, WebVideoSource {
    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        if (page > 1) {
            //没有翻页
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(url, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res = doc.select(".photoThum")
                    .map {
                        val a = it.selectFirst("a")
                        a.attr("href")
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
            "秀人网",
            "http://www.xiuren.org/",
            R.mipmap.web_xiuren,
            "图片"
        )
    }

    override fun fetchCoverContents(
        activity: FragmentActivity,
        url: String,
        page: Int,
        listener: OnDataResultListener<MutableList<ContentTitle>>
    ) {
        val realUrl = url.replace(".html", "-$page.html")
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res = doc.select(".loop")
                    .map {
                        val divContent = it.selectFirst(".contentRule")
                        val a = divContent.selectFirst("a")
                        val detailUrl = a.attr("href")
                        val title = a.attr("title")
                        val img = a.selectFirst("img")
                        val coverUrl = img.attr("src")
                        if (title.contains("视频")) {

                            ContentTitle(title, "", detailUrl, coverUrl, Category.TYPE_VIDEO)
                        } else {

                            ContentTitle(title, "", detailUrl, coverUrl, Category.TYPE_PICTURE)
                        }
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
        if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        }
        RetrofitManager.getWebsiteHtml(homePageUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                try {
                    val doc = Jsoup.parse(html)
                    val res = doc.selectFirst(".out")
                        .select("li")
                        .map {
                            it.selectFirst("a")
                        }
                        .filter {
                            val except = arrayOf("删前留名", "联系秀人", "留言板")
                            it.text() !in except && it.attr("target") != "_blank"
                        }.map {
                            val title = it.text()
                            if (title == "推女郎视频") {
                                Category(it.text(), it.attr("href"), Category.TYPE_VIDEO)
                            } else {
                                Category(it.text(), it.attr("href"), Category.TYPE_PICTURE)
                            }
                        }.toMutableList()
                    listener.onSuccess(res)
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener.onError("网站结构可能有变动")
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
                val p = Pattern.compile("setup\\((.*?)\\)")
                val m = p.matcher(html)
                if (!m.find()) {
                    listener.onError("该栏目没有视频")
                    return
                }
                val playJson = m.group(1)
                //该网站视频数量不一时返回的json格式不一样
                var playJsonVo: PlayJson
                playJsonVo = Gson().fromJson(playJson, PlayJson::class.java)
                if (playJsonVo.playlist == null) {
                    val playFile = Gson().fromJson(playJson, PlayFile::class.java)
                    playJsonVo = PlayJson(listOf(playFile))
                }
                val res = playJsonVo.playlist!!
                    .map {
                        val playUrl = PlayUrl(true, "mp4", "480p", it.file)
                        val playList = listOf(playUrl)
                        PlayContent(playList, it.title, it.description)
                    }.toMutableList()
                listener.onSuccess(res)

            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    data class PlayJson(var playlist: List<PlayFile>?)


    data class PlayFile(var file: String, var title: String, var description: String)
}