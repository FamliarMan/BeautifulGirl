package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element

/**
 *
 * @author jianglei on 3/30/19.
 */
class TextHandler(singleRule: String) : AbstractRuleHandler(singleRule) {

    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_STRING
    }


    override fun getRuleDesc(): RuleDesc {
        return RuleDesc(null, null, null)
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val res = mutableListOf<String>()
        for (e in preResult as List<Element>) {
            res.add(e.text())
        }
        return res
    }
}