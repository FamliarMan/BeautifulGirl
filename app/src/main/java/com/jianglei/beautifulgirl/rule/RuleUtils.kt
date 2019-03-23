package com.jianglei.beautifulgirl.rule

import com.jianglei.ruleparser.RuleParser
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
fun PageRule.getNextUrl(parser: RuleParser, baseUrl: String): String? {
    if (this.isFromHtml) {
        if (nextUrlRule == null) {
            throw IllegalArgumentException("当直接从网页抓取下一页的url时，nextUrlRule不能为空")
        }
        val res = parser.getStrings(nextUrlRule!!)
        if (res.isEmpty()) {
            return null
        }
        return UrlUtils.getFullUrl(
            UrlUtils.getWebHost(baseUrl),
            UrlUtils.getWebProtocol(baseUrl),
            res[0]
        )
    }
    if (combinedUrl == null || paramRule == null) {
        throw IllegalArgumentException("baseUrl 或combinedUrl paramRule 不能为空")
    }
    var combinedUrl = this.combinedUrl!!
    if (!combinedUrl.contains("{baseUrl}") || !combinedUrl.contains("{page}")) {
        throw IllegalArgumentException("combinedUrl 必须包含{baseUrl} 和{page}")
    }
    val pages = parser.getStrings(this.paramRule!!)
    if (pages.isEmpty()) {
        return null
    }
    combinedUrl = combinedUrl.replace("{baseUrl}", baseUrl)
    combinedUrl = combinedUrl.replace("{page}", pages[0])
    return combinedUrl

}
