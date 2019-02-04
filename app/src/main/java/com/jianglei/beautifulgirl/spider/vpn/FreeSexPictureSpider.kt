package com.jianglei.beautifulgirl.spider.vpn

import android.text.TextUtils
import android.util.Log
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
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * @author jianglei on 2/2/19.
 */
@WebSource(false)
class FreeSexPictureSpider : WebPictureSource {
    private var supportPageContent = false
    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        var realUrl = url
        if (page > 1 && supportPageContent) {
            realUrl = "$url?page=$page"
        } else if (page > 1) {
            listener.onSuccess(ArrayList())
            return
        } else {
            //page为1时
            supportPageContent = false
        }
        val header = hashMapOf(
            ":authority" to "www.sepic.xyz",
            "upgrade-insecure-requests" to "1"
            )
        RetrofitManager.getWebsiteHtml(realUrl, header,object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res = ArrayList<String>()

                val cardBody = doc.selectFirst(".card-body")
                if (cardBody == null) {
                    val demoGallery = doc.selectFirst(".demo-gallery")
                    if (demoGallery != null) {
                        res.addAll(getGalleryUrls(demoGallery))
                    }
                } else {
                    res.addAll(getCardUrls(cardBody))
                }
                if (page == 1 && doc.selectFirst(".pagination") != null) {
                    //支持翻页
                    supportPageContent = true
                }

                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    override fun fetchWebsite(): WebsiteDescVo {
        return WebsiteDescVo(
            "FreeSexPicture",
            "https://www.sepic.xyz/part/picarea",
            R.mipmap.web_free_sex_pic,
            "图片"
        )
    }

    override fun fetchCoverContents(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {
        val realUrl = "$url?page=$page"
        RetrofitManager.getWebsiteHtml(realUrl, object : OnWebResultListener {
            override fun onSuccess(html: String) {
                val doc = Jsoup.parse(html)
                val res: MutableList<ContentTitle> = ArrayList()
                //这个页面有两种布局
                val tbody = doc.selectFirst("tbody")

                if (tbody == null) {
                    //继续检测是否是流式布局
                    val articles = doc.select(".white-panel")
                    res.addAll(getFlexContents(articles))

                } else {
                    res.addAll(getTableContents(tbody.select("tr")))
                }
                listener.onSuccess(res)
            }

            override fun onError(code: Int, msg: String) {
                listener.onError(msg)
            }
        })
    }

    //获取表格布局状态的所有贴子
    private fun getTableContents(elements: Elements): List<ContentTitle> {
        return elements
            .map {
                val td = it.selectFirst("td")
                val a = td.selectFirst("a")
                val url = a.attr("href")
                val title = a.text()
                ContentTitle(title, "", url, "", Category.TYPE_PICTURE)
            }.toMutableList()
    }

    //获取相册形式的图片的所有url
    private fun getGalleryUrls(demoGallery: Element): MutableList<String> {
        return demoGallery.selectFirst("ul")
            .select("li")
            .map {
                it.attr("data-src")
            }.toMutableList()

    }

    //获取卡片形式的图片所有url
    private fun getCardUrls(cardBody: Element): MutableList<String> {
        return cardBody.select("img")
            .map {
                it.attr("src")
            }.toMutableList()

    }

    //获取流式布局的贴子
    private fun getFlexContents(elements: Elements): List<ContentTitle> {
        return elements
            .filter {
                val img = it.selectFirst("img")
                img != null
            }.map {
                val a = it.selectFirst("a")
                val detailUrl = a.attr("href")
                val img = it.selectFirst("img")

                val coverUrl = img.attr("src")

                val h1 = it.selectFirst("h1")
                var title = ""
                if (h1 == null) {
                    title = it.selectFirst("p").text()
                } else {
                    //内容描述放在<h1>标签中
                    title = h1.selectFirst("a").text()

                }
                ContentTitle(title, "", detailUrl, coverUrl, Category.TYPE_PICTURE)
            }.toMutableList()

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
                val res = doc.select(".list-group-item")
                    .filter {

                        val title = it.selectFirst("a").text()
                        !title.contains("欧美激情")
                    }
                    .map {
                        val a = it.selectFirst("a")
                        val title = a.text()
                        val url = a.attr("href")
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