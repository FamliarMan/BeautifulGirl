package com.jianglei.girlshow.rule

import com.jianglei.ruleparser.HtmlParser
import org.jsoup.nodes.Document
import utils.UrlUtils
import java.lang.IllegalArgumentException

/**
 * @author jianglei on 3/23/19.
 */

/**
 * 根据一个PageRule获取下一页的具体地址，如果没有下一页，返回null
 * [parser] 是当前页面的解析器
 * [baseUrl] 是当前页面除掉分页参数外的其他url
 */
fun PageRule.getNextUrl(parser: HtmlParser, baseUrl: String, nextPage: Int?, doc: Document): String? {
    if (this.fromHtml) {
        if (nextUrlRule.isNullOrBlank()) {
            throw IllegalArgumentException("当直接从网页抓取下一页的url时，nextUrlRule不能为空")
        }
        var newUrlRule = nextUrlRule!!
        if (this.nextUrlRule!!.contains("{page}")) {
            if (nextPage == null) {
                throw IllegalArgumentException("规则中使用了{page}占位符后，必须传入nextPage")
            }
            newUrlRule = nextUrlRule!!.replace("{page}", nextPage.toString())

        }
        val res = parser.getStrings(newUrlRule, doc)
        if (res.isEmpty()) {
            return null
        }
        return UrlUtils.getFullUrl(
            baseUrl,
            res[0]
        )
    }
    if (combinedUrl.isNullOrBlank() || paramRule.isNullOrBlank()) {
        throw IllegalArgumentException("baseUrl 或combinedUrl paramRule 不能为空")
    }
    var combinedUrl = this.combinedUrl!!
    if (!combinedUrl.contains("{baseUrl}") || !combinedUrl.contains("{page}")) {
        throw IllegalArgumentException("combinedUrl 必须包含{baseUrl} 和{page}")
    }
    val pages = parser.getStrings(this.paramRule!!, doc)
    if (pages.isEmpty()) {
        return null
    }
    combinedUrl = combinedUrl.replace("{baseUrl}", baseUrl)
    combinedUrl = combinedUrl.replace("{page}", pages[0])
    return combinedUrl

}


/**
 * 对分页的url规则进行预处理
 */
fun PageRule?.preHandlUrlRule(nextPage: Int): String {
    if (this == null) {
        return ""
    }
    if (nextUrlRule.isNullOrBlank()) {
        return ""
    }
    if (nextUrlRule!!.contains("{page}")) {
        return nextUrlRule!!.replace("{page}", nextPage.toString())
    }
    return nextUrlRule!!
}

fun PageRule.getCombinedUrl(parser: HtmlParser, baseUrl: String, doc: Document): String {

    if (combinedUrl.isNullOrBlank() || paramRule.isNullOrBlank()) {
        throw IllegalArgumentException("baseUrl 或combinedUrl paramRule 不能为空")
    }
    var combinedUrl = this.combinedUrl!!
    if (!combinedUrl.contains("{baseUrl}") || !combinedUrl.contains("{page}")) {
        throw IllegalArgumentException("combinedUrl 必须包含{baseUrl} 和{page}")
    }
    val pages = parser.getStrings(this.paramRule!!, doc)
    if (pages.isEmpty()) {
        return ""
    }
    combinedUrl = combinedUrl.replace("{baseUrl}", baseUrl)
    combinedUrl = combinedUrl.replace("{page}", pages[0])
    return combinedUrl
}


fun WebRule.getBaseUrl(): String {
    return if (!this.homeUrl.isBlank()) {
        homeUrl
    } else {
        url
    }
}
