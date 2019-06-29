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
import org.jsoup.nodes.Document
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
    private val curParser = HtmlParser()
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
                        getCategory(html, newPage,
                            object : OnDataResultListener2<List<Category>, String> {
                                override fun onSuccess(categories: List<Category>?, nextUrl: String?) {

                                    LogUtil.d("----------------解析分类结果如下,数量：${categories!!.size}---------------------")
                                    for (category in categories) {
                                        LogUtil.d(
                                            category.title + " "
                                                    + category.url + " " + category.coverUrl
                                        )
                                    }
                                    //准备下一页的地址
                                    if (webRule.categoryRule!!.supportPage) {
                                        nextCategoryUrl = nextUrl
                                    }
                                    if (nextUrl.isNullOrBlank()) {
                                        nextCategoryUrl = ""
                                    }
                                    listener.onSuccess(categories)
                                }

                                override fun onError(msg: String) {
                                    listener.onError(msg)
                                }
                            })
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

    private fun getCategory(
        html: String,
        page: Int,
        listener: OnDataResultListener2<List<Category>, String>
    ) {
        val document = Jsoup.parse(html)

        val nextPageRule = webRule.categoryRule!!.pageRule.preHandlUrlRule(page + 1)
        curParser.getStringsUnitAsnyc(
            //分类名称url
            webRule.categoryRule!!.nameRule,
            //分类跳转url
            webRule.categoryRule!!.urlRule,
            //分类封面url
            webRule.categoryRule!!.imageUrlRule,
            //下一页跳转地址,
            nextPageRule,
            document = document,
            onParserSuccessListener = object : HtmlParser.OnParserSuccessListener {
                override fun parserSuccess(res: List<List<String>>) {
                    try {

                        LogUtil.d(String.format("获得一级分类名称:%d", res[0].size))
                        res[0].forEach {
                            LogUtil.d("分类名称:$it")
                        }
                        val titles = res[0]

                        val host = UrlUtils.getWebHost(webRule.url)
                        val protocol = UrlUtils.getWebProtocol(webRule.url)
                        LogUtil.d("获得一级分类跳转url：" + res[1].size)
                        res[1].forEach {
                            LogUtil.d("分类跳转url:$it")
                        }
                        val urls = res[1]
                            .map {
                                UrlUtils.getFullUrl(host, protocol, it)
                            }.toList()

                        var coverUrl: List<String>? = null

                        if (!webRule.categoryRule!!.imageUrlRule.isNullOrBlank()) {
                            LogUtil.d("获得一级分类封面url：" + res[2].size)
                            res[2].forEach {
                                LogUtil.d("分类封面url:$it")
                            }
                            coverUrl = res[2]
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
                        val categories = mutableListOf<Category>()
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
                            categories.add(category)
                        }


                        //准备下一页的地址
                        var nextCategoryUrl: String? = null
                        if (webRule.categoryRule!!.supportPage) {
                            if (webRule.categoryRule!!.pageRule!!.fromHtml) {
                                //能从html页面抓取
                                if (res[3].isEmpty()) {
                                    nextCategoryUrl = ""
                                } else {
                                    nextCategoryUrl = UrlUtils.getFullUrl(webRule.getBaseUrl(), res[3][0])
                                }
                            } else {
                                //需要我们自己组合
                                nextCategoryUrl = webRule.categoryRule!!.pageRule!!.getCombinedUrl(
                                    curParser, webRule.getBaseUrl(), document
                                )
                            }
                            LogUtil.d("解析分类下一页请求地址：$nextCategoryUrl")
                        }
                        listener.onSuccess(categories, nextCategoryUrl)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        listener.onError(e.toString())

                    }
                }

                override fun parserError(msg: String) {
                    listener.onError(msg)
                }
            }

        )
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
                    val document = Jsoup.parse(html)
                    getContentTitle(
                        document,
                        curRule,
                        newPage,
                        nextCoverUrl!!,
                        object : OnDataResultListener2<List<ContentTitle>, String> {
                            override fun onSuccess(data1: List<ContentTitle>?, data2: String?) {

                                val res = data1!!
                                LogUtil.d("----------------解析封面结果如下,数量：${res.size} ---------------------")
                                res.forEach {
                                    LogUtil.d("name:" + it.title + " url:" + it.detailUrl + " img:" + it.coverUrl)
                                }
                                //解析下一页的地址
                                nextCoverUrl = data2
                                listener.onSuccess(res)
                            }

                            override fun onError(msg: String) {
                                listener.onError(msg)
                            }
                        })
                }

                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }
            }, webRule.encoding
        )

    }

    private fun getContentTitle(
        document: Document, curRule: CategoryRule, curPage: Int, curPageUrl: String,
        listener: OnDataResultListener2<List<ContentTitle>, String>
    ) {
        val nextPageRule = curRule.pageRule.preHandlUrlRule(curPage + 1)
        curParser.getStringsUnitAsnyc(
            //二级分类url规则,
            curRule.nameRule,
            //二级分类url规则
            curRule.urlRule,
            //二级分类描述url
            curRule.descRule,
            //二级分类封面url
            curRule.imageUrlRule,
            //下一页的地址规则
            nextPageRule,
            document = document,
            onParserSuccessListener = object : HtmlParser.OnParserSuccessListener {
                override fun parserSuccess(res: List<List<String>>) {
                    try {

                        val names = res[0]
                        var urls = res[1]
                        if (names.size != urls.size) {
                            throw IllegalArgumentException("解析到的封面的名称和url数量不匹配")
                        }
                        urls = urls.map {
                            UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
                        }.toList()
                        var descs: List<String>? = null
                        var coverUrls: List<String>? = null
                        if (!curRule.descRule.isNullOrBlank()) {
                            descs = res[2]
                        }
                        if (!curRule.imageUrlRule.isNullOrBlank()) {
                            LogUtil.d("开始解析二级分类封面url：")
                            coverUrls = res[3].map {
                                UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
                            }.toList()
                        }
                        val contents = mutableListOf<ContentTitle>()

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
                            contents.add(contentTitle)
                        }


                        //解析下一页的地址
                        var nextPage: String? = null
                        if (curRule.supportPage) {
                            if (curRule.pageRule!!.fromHtml) {
                                //能从html页面抓取
                                if (res[4].isEmpty()) {
                                    nextPage = ""
                                } else {
                                    nextPage = UrlUtils.getFullUrl(baseCoverUrl, res[4][0])
                                }

                            } else {
                                nextPage = curRule.pageRule!!.getCombinedUrl(
                                    curParser, baseCoverUrl, document
                                )
                            }
                            LogUtil.d("解析封面下一页请求地址：$nextPage")
                        } else {
                            nextPage = ""
                        }
                        listener.onSuccess(contents, nextPage)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        listener.onError(e.toString())

                    }
                }

                override fun parserError(msg: String) {
                    listener.onError(msg)
                }
            }
        )
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
                    val document = Jsoup.parse(html)
                    getContents(document, newPage, nextContentUrl!!,
                        object : OnDataResultListener2<List<ContentVo>, String> {
                            override fun onSuccess(data1: List<ContentVo>?, data2: String?) {

                                val res = data1!!

                                LogUtil.d("----------------解析具体地址如下:数量：${res.size}---------------------")
                                res.forEach {
                                    LogUtil.d("name:" + it.name + " url:" + it.url)
                                }
                                nextContentUrl = if (data2.isNullOrBlank()) {
                                    ""
                                } else {
                                    data2
                                }
                                listener.onSuccess(res)
                            }

                            override fun onError(msg: String) {
                                listener.onError(msg)
                            }
                        })
                    try {
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

    private fun getContents(
        document: Document, curPage: Int, curUrl: String,
        listener: OnDataResultListener2<List<ContentVo>, String>
    ) {

        val nextPageRule = webRule.contentRule!!.pageRule.preHandlUrlRule(curPage + 1)
        curParser.getStringsUnitAsnyc(
            //内容url规则
            webRule.contentRule!!.urlRule,
            //内容名称url
            webRule.contentRule!!.nameRule,
            //下一页的地址规则
            nextPageRule,
            document = document,
            onParserSuccessListener = object : HtmlParser.OnParserSuccessListener {
                override fun parserSuccess(res: List<List<String>>) {
                    LogUtil.d("解析具体内容的url")
                    var urls = curParser.getStringsUnit(webRule.contentRule!!.urlRule, document)
                    urls = urls.map {
                        UrlUtils.getFullUrl(webRule.getBaseUrl(), it)
                    }
                    var names: List<String>? = null
                    if (!webRule.contentRule!!.nameRule.isBlank()) {
                        LogUtil.d("解析具体内容的名称")
                        names = curParser.getStringsUnit(webRule.contentRule!!.nameRule, document)
                    }
                    val contents = mutableListOf<ContentVo>()
                    for (i in 0 until urls.size) {
                        val name = if (names == null || i >= names.size) {
                            null
                        } else {
                            names[i]
                        }
                        contents.add(ContentVo(name, urls[i]))

                    }

                    //解析下一页的地址
                    var nextUrl: String? = null
                    if (webRule.contentRule!!.supportPage) {
                        if (webRule.contentRule!!.pageRule!!.fromHtml) {
                            if (res[2].isEmpty()) {
                                nextUrl = ""
                            } else {
                                nextUrl = res[2][0]
                            }
                        } else {
                            nextUrl = webRule.contentRule!!.pageRule!!
                                .getCombinedUrl(curParser, curUrl, document)
                        }
                        LogUtil.d("解析具体内容的下一页地址成功：$nextUrl")
                    }
                    listener.onSuccess(contents, nextUrl)
                }

                override fun parserError(msg: String) {
                    listener.onError(msg)
                }
            }
        )
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
