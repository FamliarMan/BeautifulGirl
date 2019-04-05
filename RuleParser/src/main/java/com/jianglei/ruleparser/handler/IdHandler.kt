package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 *
 * @author jianglei on 3/30/19.
 */
class IdHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    /**
     * 通过id正则匹配
     */
    private val idRegexPattern = Regex("@id:regex<(.*)>(\\[(\\d)])?")
    /**
     * 通过id完全匹配
     */
    private val idNamePattern = Regex("@id:<(.*)>(\\[(\\d)])?")

    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT
    }


    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, idRegexPattern, idNamePattern)
    }

    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val res = mutableListOf<Element>()
        val ruleDesc = getRuleDesc()
        @Suppress("UNCHECKED_CAST")
        for (e in preResult as List<Element>) {
            val elements: Elements
            if (ruleDesc.regx != null) {
                elements = e.select("[id~=${ruleDesc.regx}]")
            } else {
                elements = Elements(e.getElementById(ruleDesc.name))
            }
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