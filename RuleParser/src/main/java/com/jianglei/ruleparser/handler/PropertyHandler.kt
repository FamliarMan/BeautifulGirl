package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element

/**
 *
 * @author jianglei on 3/30/19.
 */
class PropertyHandler(singleRule: String) : AbstractRuleHandler(singleRule) {

    private val propertyPattern = Regex("@property:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_STRING
    }


    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, propertyPattern)
    }

    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val res = mutableListOf<String>()
        val ruleDesc = getRuleDesc()

        @Suppress("UNCHECKED_CAST")
        for (e in preResult as List<Element>) {
            res.add(e.attr(ruleDesc.name))
        }
        return res


    }
}