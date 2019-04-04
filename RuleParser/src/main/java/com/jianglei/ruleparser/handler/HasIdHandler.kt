package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * @author jianglei on 4/4/19.
 */
class HasIdHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    private val hasIdRegex = Regex("^@hasId:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {

        val filterCondition = getRuleDesc().name!!.split(",")
        val res = (preResult as List<Element>).filter {
            var hasAllIds = true
            for (id in filterCondition) {
                if (it.getElementById(id) == null) {
                    hasAllIds = false
                    break
                }
            }
            hasAllIds
        }.toList()
        return Elements(res)
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, hasIdRegex)
    }
}