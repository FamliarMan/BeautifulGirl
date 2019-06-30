package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.RuleKeyWord
import com.jianglei.ruleparser.data.RetrofitManager
import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * @http
 */
class HttpHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    override fun acceptType(): List<Int> {
        return listOf(TYPE_STRING)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT

    }

    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        if (lastRuleHandler == null) {
            throw  IllegalSyntaxException(RuleKeyWord.HTTP + "没有接收任何url")
        } else if (preResult.isEmpty() || !(preResult[0] as String).contains("http")) {
            throw  IllegalSyntaxException(RuleKeyWord.HTTP + "没有接收到任何正确的url")
        }
        val url = preResult[0] as String
        val html = RetrofitManager.getWebsiteHtmlSync(url, "utf-8")
        val res = mutableListOf<Element>()
        val elements = Jsoup.parse(html).children()
        res.addAll(elements)
        return res
    }

    override fun getRuleDesc(): RuleDesc {

        return RuleDesc(null, null, null)
    }
}