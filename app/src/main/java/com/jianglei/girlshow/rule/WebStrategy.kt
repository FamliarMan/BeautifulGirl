package com.jianglei.girlshow.rule

import androidx.fragment.app.FragmentActivity
import com.jianglei.girlshow.data.*
import com.jianglei.girlshow.vo.Category
import com.jianglei.girlshow.vo.ContentTitle
import com.jianglei.girlshow.vo.SearchVideoKeyWord
import com.jianglei.ruleparser.*
import com.jianglei.videoplay.ContentVo
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.UrlUtils
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * @author jianglei on 3/17/19.
 */
class WebStrategy(val webRule: WebRule) {
    private var webGetter: WebGetter = WebGetter()
    var nextCategoryUrl: String? = webRule.url
    var nextCoverUrl: String? = null
    var nextContentUrl: String? = null
    private var call: Call<ResponseBody>? = null
    /**
     * 封面的基本url（出去分页参数的url）
     */
    private lateinit var baseCoverUrl: String
    private lateinit var baseContentUrl: String
    private lateinit var curParser: HtmlParser
    /**
     * 解析某个网站的所有分类栏目
     */
    fun fetchAllCategory(
        activity: FragmentActivity,
        page: Int,
        listener: OnDataResultListener<List<Category>>
    ) {

        if (page == 1) {
            nextCategoryUrl = webRule.url
        }
        var newPage = page
        if (webRule.categoryRule == null) {
            throw IllegalArgumentException("第一级分类规则不能为空")
        }
        if (webRule.categoryRule!!.supportPage && webRule.categoryRule!!.pageRule != null &&
            webRule.categoryRule!!.pageRule!!.startPage != null
        ) {
            newPage = webRule.categoryRule!!.pageRule!!.startPage!! + page - 1
        }
        if (nextCategoryUrl == null) {
            listener.onSuccess(emptyList())
            return
        }
        LogUtil.d("开始解析分类页面：$nextCategoryUrl")
        webGetter.getWebsiteHtml(
            activity, webRule.categoryRule!!.dynamicRender, nextCategoryUrl!!,
            emptyMap(), object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getCategory(html)
                        LogUtil.d("----------------解析分类结果如下,数量：${res.size}---------------------")
                        for (category in res) {
                            LogUtil.d(
                                category.title + " "
                                        + category.url + " " + category.coverUrl
                            )
                        }
                        //准备下一页的地址
                        if (webRule.categoryRule!!.supportPage) {
                            nextCategoryUrl = webRule.categoryRule!!.pageRule!!
                                .getNextUrl(curParser, webRule.getBaseUrl(), newPage + 1)
                            LogUtil.d("解析分类下一页请求地址：$nextCategoryUrl")
                        } else {
                            nextCategoryUrl = ""
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
        val ruleParser = HtmlParser(document)
        LogUtil.d("开始解析一级分类名称：")
        val titles = ruleParser.getStringsUnit(webRule.categoryRule!!.nameRule)

        val host = UrlUtils.getWebHost(webRule.url)
        val protocol = UrlUtils.getWebProtocol(webRule.url)
        LogUtil.d("开始解析一级分类跳转url：")
        val urls = ruleParser.getStringsUnit(webRule.categoryRule!!.urlRule)
            .map {
                UrlUtils.getFullUrl(host, protocol, it)
            }.toList()
        var coverUrl: List<String>? = null

        if (!webRule.categoryRule!!.imageUrlRule.isNullOrBlank()) {
            LogUtil.d("开始解析一级分类封面url：")
            coverUrl = ruleParser.getStringsUnit(webRule.categoryRule!!.imageUrlRule!!)
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
     * 解析一个分类下的所有内容封面
     * [startUrl] 在不是第一页时不用传，否则出错
     */
    fun fetchAllCover(
        activity: FragmentActivity,
        page: Int,
        startUrl: String?,
        isSearch: Boolean,
        listener: OnDataResultListener<List<ContentTitle>>
    ) {
        if (nextCoverUrl == null && page != 1) {
            listener.onSuccess(emptyList())
            return
        }
        val curRule: CategoryRule
        if (isSearch) {
            curRule = webRule.searchRule!!.resultRule!!
        } else {
            curRule = webRule.coverRule!!
        }

        if (page == 1) {
            if (!curRule.realRequestUrlRule.isNullOrBlank()) {
                nextCoverUrl = curRule.realRequestUrlRule!!
                    .replace("{baseUrl}", startUrl!!)
                baseCoverUrl = nextCoverUrl!!
            } else {
                nextCoverUrl = startUrl
                baseCoverUrl = startUrl!!
            }
        }
        var newPage = page
        if (curRule.supportPage && curRule.pageRule != null &&
            curRule.pageRule!!.startPage != null
        ) {
            newPage = curRule.pageRule!!.startPage!! + page - 1
        }
        LogUtil.d("开始解析分类下级封面页面：$nextCoverUrl")
        webGetter.getWebsiteHtml(
            activity,
            curRule.dynamicRender,
            nextCoverUrl!!,
            emptyMap(),
            object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getContentTitle(html, curRule)
                        LogUtil.d("----------------解析封面结果如下,数量：${res.size} ---------------------")
                        res.forEach {
                            LogUtil.d("name:" + it.title + " url:" + it.detailUrl + " img:" + it.coverUrl)
                        }
                        //解析下一页的地址
                        if (curRule.supportPage) {
                            nextCoverUrl = curRule.pageRule!!
                                .getNextUrl(curParser, webRule.getBaseUrl(), newPage + 1)
                            LogUtil.d("解析封面下一页请求地址：$nextCoverUrl")
                        } else {
                            nextCoverUrl = ""
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

    private fun getContentTitle(html: String, curRule: CategoryRule): List<ContentTitle> {
        val parser = HtmlParser(Jsoup.parse(html))
        LogUtil.d("开始解析二级分类名称：")
        val names = parser.getStringsUnit(curRule.nameRule)
        LogUtil.d("开始解析二级分类url：")
        var urls = parser.getStringsUnit(curRule.urlRule)
        if (names.size != urls.size) {
            throw IllegalArgumentException("解析到的封面的名称和url数量不匹配")
        }
        urls = urls.map {
            UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
        }.toList()
        var descs: List<String>? = null
        var coverUrls: List<String>? = null
        if (!curRule.descRule.isNullOrBlank()) {
            LogUtil.d("开始解析二级分类描述：")
            descs = parser.getStringsUnit(curRule.descRule!!)
        }
        if (!curRule.imageUrlRule.isNullOrBlank()) {
            LogUtil.d("开始解析二级分类封面url：")
            coverUrls = parser.getStringsUnit(curRule.imageUrlRule!!).map{
                UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
            }.toList()
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


    fun fetchAllContents(
        activity: FragmentActivity,
        page: Int,
        startUrl: String?,
        listener: OnDataResultListener<List<ContentVo>>
    ) {
        if (nextContentUrl == null && page != 1) {
            listener.onSuccess(emptyList())
            return
        }

        if (page == 1) {

            if (!webRule.contentRule!!.realRequestUrlRule.isNullOrBlank()) {
                nextContentUrl = webRule.contentRule!!.realRequestUrlRule!!
                    .replace("{baseUrl}", startUrl!!)
                baseContentUrl = nextContentUrl!!
            } else {
                nextContentUrl = startUrl
                baseContentUrl = startUrl!!
            }
        } else if (!webRule.contentRule!!.supportPage) {
            listener.onSuccess(emptyList())
            return
        }
        LogUtil.d("开始解析具体内容:$nextContentUrl")

        var newPage = page
        if (webRule.contentRule!!.supportPage && webRule.contentRule!!.pageRule != null &&
            webRule.contentRule!!.pageRule!!.startPage != null
        ) {
            newPage = webRule.contentRule!!.pageRule!!.startPage!! + page - 1
        }
        webGetter.getWebsiteHtml(
            activity,
            webRule.contentRule!!.dynamicRender,
            nextContentUrl!!,
            emptyMap(),
            object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getContents(html)

                        LogUtil.d("----------------解析具体地址如下:数量：${res.size}---------------------")
                        res.forEach {
                            LogUtil.d("name:" + it.name + " url:" + it.url)
                        }
                        //解析下一页的地址
                        if (webRule.contentRule!!.supportPage) {
                            nextContentUrl = webRule.contentRule!!.pageRule!!
                                .getNextUrl(curParser, webRule.getBaseUrl(), newPage + 1)
                            LogUtil.d("解析具体内容的下一页地址成功：$nextContentUrl")

                        } else {
                            nextContentUrl = ""
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

    private fun getContents(html: String): List<ContentVo> {
        val parser = HtmlParser(Jsoup.parse(html))
        LogUtil.d("解析具体内容的url")
        var urls = parser.getStringsUnit(webRule.contentRule!!.urlRule)
        urls = urls.map {
            UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
        }
        var names: List<String>? = null
        if (!webRule.contentRule!!.nameRule.isBlank()) {
            LogUtil.d("解析具体内容的名称")
            names = parser.getStringsUnit(webRule.contentRule!!.nameRule)
        }
        val res = mutableListOf<ContentVo>()
        for (i in 0 until urls.size) {
            val name = if (names == null || i >= names.size) {
                null
            } else {
                names[i]
            }
            res.add(ContentVo(name, urls[i]))

        }
        curParser = parser
        return res
    }

    fun fetchSearchSuggest(searchTxt: String, listener: OnDataResultListener<List<SearchVideoKeyWord>>) {
        if (webRule.searchRule == null) {
            throw IllegalArgumentException("当前网站不支持搜索")
        }
        if (webRule.searchRule!!.suggestUrl == null) {
            //当前网页不支持搜索建议
            listener.onSuccess(emptyList())
            return
        }
        val url = webRule.searchRule!!.suggestUrl!!.replace(RuleKeyWord.SEARCH_TXT, searchTxt)
        call = RetrofitManager.retrofit.create(WebService::class.java)
            .searchSuggest(url)
        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t.toString())
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val body = response.body()
                    if (body == null) {
                        listener.onError("解析搜索建议失败")
                        return
                    }
                    val res = getSuggest(body.string())
                    LogUtil.d("--------------------解析搜索建议如下：-------------------")
                    res.forEach {
                        LogUtil.d("key:${it.N} time:${it.R}")
                    }
                    listener.onSuccess(res)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    listener.onError(e.toString())

                }
            }
        })

    }

    private fun getSuggest(json: String): List<SearchVideoKeyWord> {
        if (webRule.searchRule!!.suggestKeyRule == null) {
            throw IllegalArgumentException("搜索建议的suggestKeyRule不能为空")
        }
        val jsonParser = JsonParser(json)
        val keyRes = jsonParser.getStrings(
            webRule.searchRule!!.suggestKeyRule!!,
            json
        )
        var countsRes = emptyList<String>()
        if (!webRule.searchRule!!.suggestTimeRule.isNullOrBlank()) {
            countsRes = jsonParser.getStrings(
                webRule.searchRule!!.suggestTimeRule!!,
                json
            )
        }

        val res = mutableListOf<SearchVideoKeyWord>()
        for (i in 0 until keyRes.size) {
            val count = if (countsRes.isEmpty() || i >= countsRes.size) {
                ""
            } else {
                countsRes[i]
            }
            res.add(SearchVideoKeyWord(keyRes[i], count))
        }
        return res
    }

    fun cancel() {
        webGetter.cancel()
        call?.cancel()
    }
}
