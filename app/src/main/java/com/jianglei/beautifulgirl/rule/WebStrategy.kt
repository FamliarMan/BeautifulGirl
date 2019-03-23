package com.jianglei.beautifulgirl.rule

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebViewResultListener
import com.jianglei.beautifulgirl.data.WebGetter
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.ruleparser.RuleParser
import org.jsoup.Jsoup
import utils.UrlUtils
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * @author jianglei on 3/17/19.
 */
class WebStrategy(private val webRule: WebRule) {
    private var webGetter: WebGetter = WebGetter()
    private var nextCategoryUrl: String? = webRule.categoryRule.url
    private var nextCoverUrl: String? = null
    /**
     * 封面的基本url（出去分页参数的url）
     */
    private lateinit var baseCoverUrl: String
    private lateinit var curParser: RuleParser
    /**
     * 获取某个网站的所有分类栏目
     */
    fun fetchAllCategory(
        activity: FragmentActivity,
        listener: OnDataResultListener<List<Category>>
    ) {
        if (nextCategoryUrl == null) {
            listener.onSuccess(emptyList())
            return
        }
        webGetter.getWebsiteHtml(
            activity, webRule.dynamicRender, nextCategoryUrl!!,
            emptyMap(), object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getCategory(html)
                        for (category in res) {
                            Log.d(
                                "longyi", category.title + " "
                                        + category.url + " " + category.coverUrl
                            )
                        }
                        //准备下一页的地址
                        if (webRule.categoryRule.pageRule != null) {
                            nextCategoryUrl = webRule.categoryRule.pageRule!!
                                .getNextUrl(curParser, webRule.categoryRule.url)
                        }
                        listener.onSuccess(res)
                    } catch (e: Throwable) {
                        listener.onError(e.toString())
                    }
                }

                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }
            },
            webRule.encoding
        )

    }

    private fun getCategory(html: String): MutableList<Category> {
        val document = Jsoup.parse(html)
        val ruleParser = RuleParser(document)
        val titles = ruleParser.getStrings(webRule.categoryRule.nameRule)

        val host = UrlUtils.getWebHost(webRule.categoryRule.url)
        val protocol = UrlUtils.getWebProtocol(webRule.categoryRule.url)
        val urls = ruleParser.getStrings(webRule.categoryRule.urlRule)
            .map {
                UrlUtils.getFullUrl(host, protocol, it)
            }.toList()
        var coverUrl: List<String>? = null

        if (webRule.categoryRule.imageUrlRule != null) {
            coverUrl = ruleParser.getStrings(webRule.categoryRule.imageUrlRule!!)
                .map {
                    UrlUtils.getFullUrl(host, protocol, it)
                }.toList()
        }
        if (titles.isEmpty()) {
            throw IllegalArgumentException("分类的名称规则填写有误，没有捕捉到任何分类名称")
        }
        if (urls.isEmpty()) {
            throw IllegalArgumentException("分类的跳转url规则填写有误，没有捕捉到任何跳转url")
        }
        if (coverUrl != null && coverUrl.isEmpty()) {
            throw IllegalArgumentException("分类的封面url规则填写有误，没有捕捉到任何跳转url")
        }

        if (titles.size != urls.size) {
            throw IllegalStateException("捕捉的分类的名称数量和跳转url数量不一致,可能规则填写有误")
        }
        val res = mutableListOf<Category>()
        val type = if (webRule.type == RuleConstants.TYPE_VIDEO) {
            Category.TYPE_VIDEO
        } else {
            Category.TYPE_PICTURE
        }
        for (i in titles.indices) {

            val category = Category(titles[i], urls[i], type)
            if (coverUrl != null && i < coverUrl.size) {
                category.coverUrl = coverUrl[i]
            }
            res.add(category)
        }
        curParser = ruleParser
        return res
    }

    /**
     * 获取一个分类下的所有内容封面
     * [startUrl] 在不是第一页时不用传，否则出错
     */
    fun fetchAllCover(
        activity: FragmentActivity,
        page: Int,
        startUrl: String?,
        listener: OnDataResultListener<List<ContentTitle>>
    ) {
        if (nextCoverUrl == null && page != 1) {
            listener.onSuccess(emptyList())
            return
        }
        if (page == 1) {
            nextCoverUrl = startUrl
            baseCoverUrl = startUrl!!
        }
        webGetter.getWebsiteHtml(
            activity,
            webRule.coverRule.dynamicRender,
            nextCoverUrl!!,
            emptyMap(),
            object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getContentTitle(html)
                        res.forEach {
                            Log.d("longyi", "name:" + it.title + " url:" + it.detailUrl + " img:" + it.coverUrl)
                        }
                        //获取下一页的地址
                        if (webRule.coverRule.pageRule != null) {
                            nextCoverUrl = webRule.coverRule.pageRule!!
                                .getNextUrl(curParser, baseCoverUrl)

                        }
                        listener.onSuccess(res)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        listener.onError(e.toString())
                    }
                }

                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }
            }, webRule.encoding
        )

    }

    private fun getContentTitle(html: String): List<ContentTitle> {
        val parser = RuleParser(Jsoup.parse(html))
        val names = parser.getStrings(webRule.coverRule.nameRule)
        val urls = parser.getStrings(webRule.coverRule.targetUrlRule)
        if (names.size != urls.size) {
            throw IllegalArgumentException("获取到的封面的名称和url数量不匹配")
        }
        var descs: List<String>? = null
        var coverUrls: List<String>? = null
        if (webRule.coverRule.descRule != null) {
            descs = parser.getStrings(webRule.coverRule.descRule!!)
        }
        if (webRule.coverRule.imageUrlRule != null) {
            coverUrls = parser.getStrings(webRule.coverRule.imageUrlRule!!)
        }
        val res = mutableListOf<ContentTitle>()
        for (i in 0 until names.size) {
            val desc = if (descs == null || i >= descs.size) {
                ""
            } else {
                descs[i]
            }
            val coverImgUrl = if (coverUrls == null || i >= coverUrls.size) {
                ""
            } else {
                coverUrls[i]
            }
            val type = if (webRule.type == RuleConstants.TYPE_VIDEO) {
                Category.TYPE_VIDEO
            } else {
                Category.TYPE_PICTURE
            }
            val contentTitle = ContentTitle(names[i], desc, urls[i], coverImgUrl, type)
            res.add(contentTitle)
        }
        curParser = parser
        return res
    }

    public fun cancel() {
        webGetter.cancel()
    }
}