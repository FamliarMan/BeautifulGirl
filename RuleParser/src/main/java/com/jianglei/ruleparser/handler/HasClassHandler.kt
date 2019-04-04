package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * @author jianglei on 4/4/19.
 */
class HasClassHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    private val hasClassRegex = Regex("^@hasClass:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler,preResult)

        val filterCondition = getRuleDesc().name!!.split(",")
        val res = (preResult as List<Element>).filter {
            var hasAllClass = true

            for (className in filterCondition) {
                if (it.getElementsByClass(className).isEmpty()) {
                    hasAllClass = false
                    break
                }
            }
            hasAllClass
        }.toList()
        return Elements(res)
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, hasClassRegex)
    }
}