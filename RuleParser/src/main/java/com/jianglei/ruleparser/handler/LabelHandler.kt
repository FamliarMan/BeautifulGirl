package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 *
 * @author jianglei on 3/30/19.
 */
class LabelHandler(singleRule: String) : AbstractRuleHandler(singleRule) {

    private val labelPattern = Regex("@label:<(.*)>(\\[(\\d)])?")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT
    }


    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, labelPattern)
    }

    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val res = Elements()
        val ruleDesc = getRuleDesc()
        @Suppress("UNCHECKED_CAST")
        for (e in preResult as List<Element>) {
            val elements = e.select(ruleDesc.name)
            if (elements.size == 0) {
                continue
            }
            if (ruleDesc.index != null) {
                res.add(elements[ruleDesc.index])
            } else {
                res.addAll(elements)
            }
        }
        return res

    }
}