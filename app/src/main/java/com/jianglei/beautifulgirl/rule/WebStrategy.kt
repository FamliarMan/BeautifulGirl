package com.jianglei.beautifulgirl.rule

import androidx.fragment.app.FragmentActivity
import com.jianglei.beautifulgirl.data.*
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.SearchVideoKeyWord
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
    private var nextCategoryUrl: String? = webRule.categoryRule.url
    private var nextCoverUrl: String? = null
    private var nextContentUrl: String? = null
    private var call: Call<ResponseBody>? = null
    /**
     * 封面的基本url（出去分页参数的url）
     */
    private lateinit var baseCoverUrl: String
    private lateinit var baseContentUrl: String
    private lateinit var curParser: HtmlParser
    /**
     * 获取某个网站的所有分类栏目
     */
    fun fetchAllCategory(
        activity: FragmentActivity,
        page: Int,
        listener: OnDataResultListener<List<Category>>
    ) {

        if (page == 1) {
            nextCategoryUrl = webRule.categoryRule.url
        }
        var newPage = page
        if (webRule.categoryRule.pageRule != null &&
            webRule.categoryRule.pageRule!!.startPage != null
        ) {
            newPage = webRule.categoryRule.pageRule!!.startPage!! + page - 1
        }
        if (nextCategoryUrl == null) {
            listener.onSuccess(emptyList())
            return
        }
        LogUtil.d("开始获取分类页面：$nextCategoryUrl")
        webGetter.getWebsiteHtml(
            activity, webRule.categoryRule.dynamicRender, nextCategoryUrl!!,
            emptyMap(), object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getCategory(html)
                        LogUtil.d("----------------获取分类结果如下,数量：${res.size}---------------------")
                        for (category in res) {
                            LogUtil.d(
                                category.title + " "
                                        + category.url + " " + category.coverUrl
                            )
                        }
                        //准备下一页的地址
                        if (webRule.categoryRule.pageRule != null) {
                            nextCategoryUrl = webRule.categoryRule.pageRule!!
                                .getNextUrl(curParser, webRule.categoryRule.url, newPage + 1)
                            LogUtil.d("获取分类下一页请求地址：$nextCategoryUrl")
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
        isSearch:Boolean,
        listener: OnDataResultListener<List<ContentTitle>>
    ) {
        if (nextCoverUrl == null && page != 1) {
            listener.onSuccess(emptyList())
            return
        }
        val curRule:CategoryRule
        if(isSearch){
            curRule = webRule.searchRule!!.resultRule
        }else{
            curRule = webRule.coverRule
        }

        if (page == 1) {
            if (curRule.realRequestUrlRule != null) {
                nextCoverUrl = curRule.realRequestUrlRule!!
                    .replace("{baseUrl}", startUrl!!)
                baseCoverUrl = nextCoverUrl!!
            } else {
                nextCoverUrl = startUrl
                baseCoverUrl = startUrl!!
            }
        }
        var newPage = page
        if (curRule.pageRule != null &&
            curRule.pageRule!!.startPage != null
        ) {
            newPage = curRule.pageRule!!.startPage!! + page - 1
        }
        LogUtil.d("开始获取分类下级封面页面：$nextCoverUrl")
        webGetter.getWebsiteHtml(
            activity,
            curRule.dynamicRender,
            nextCoverUrl!!,
            emptyMap(),
            object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getContentTitle(html,curRule)
                        LogUtil.d("----------------获取封面结果如下,数量：${res.size} ---------------------")
                        res.forEach {
                            LogUtil.d("name:" + it.title + " url:" + it.detailUrl + " img:" + it.coverUrl)
                        }
                        //获取下一页的地址
                        if (curRule.pageRule != null) {
                            nextCoverUrl = curRule.pageRule!!
                                .getNextUrl(curParser, baseCoverUrl, newPage + 1)
                            LogUtil.d("获取封面下一页请求地址：$nextCoverUrl")
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

    private fun getContentTitle(html: String,curRule: CategoryRule): List<ContentTitle> {
        val parser = HtmlParser(Jsoup.parse(html))
        val names = parser.getStrings(curRule.nameRule)
        var urls = parser.getStrings(curRule.urlRule)
        if (names.size != urls.size) {
            throw IllegalArgumentException("获取到的封面的名称和url数量不匹配")
        }
        urls = urls.map {
            UrlUtils.getFullUrl(baseCoverUrl, it)
        }.toList()
        var descs: List<String>? = null
        var coverUrls: List<String>? = null
        if (curRule.descRule != null) {
            descs = parser.getStrings(curRule.descRule!!)
        }
        if (curRule.imageUrlRule != null) {
            coverUrls = parser.getStrings(curRule.imageUrlRule!!)
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

            if (webRule.contentRule.realRequestUrlRule != null) {
                nextContentUrl = webRule.contentRule.realRequestUrlRule!!
                    .replace("{baseUrl}", startUrl!!)
                baseContentUrl = nextContentUrl!!
            } else {
                nextContentUrl = startUrl
                baseContentUrl = startUrl!!
            }
        } else if (
            webRule.contentRule.pageRule == null) {
            listener.onSuccess(emptyList())
            return
        }
        LogUtil.d("开始获取具体内容:$nextContentUrl")

        var newPage = page
        if (webRule.contentRule.pageRule != null &&
            webRule.contentRule.pageRule!!.startPage != null
        ) {
            newPage = webRule.contentRule.pageRule!!.startPage!! + page - 1
        }
        webGetter.getWebsiteHtml(
            activity,
            webRule.contentRule.dynamicRender,
            nextContentUrl!!,
            emptyMap(),
            object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    try {
                        val res = getContents(html)

                        LogUtil.d("----------------获取具体地址如下:数量：${res.size}---------------------")
                        res.forEach {
                            LogUtil.d("name:" + it.name + " url:" + it.url)
                        }
                        //获取下一页的地址
                        if (webRule.contentRule.pageRule != null) {
                            nextContentUrl = webRule.contentRule.pageRule!!
                                .getNextUrl(curParser, baseContentUrl, newPage + 1)
                            LogUtil.d("获取具体内容的下一页地址成功：$nextContentUrl")

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
        var urls = parser.getStrings(webRule.contentRule.detailRule)
        urls = urls.map {
            UrlUtils.getFullUrl(baseContentUrl, it)
        }
        var names: List<String>? = null
        if (webRule.contentRule.nameRule != null) {
            names = parser.getStrings(webRule.contentRule.nameRule!!)
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
                    if(body == null){
                        listener.onError("获取搜索建议失败")
                        return
                    }
                    val res = getSuggest(body.string())
                    LogUtil.d("--------------------获取搜索建议如下：-------------------")
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
        if (webRule.searchRule!!.suggestTimeRule != null) {
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
