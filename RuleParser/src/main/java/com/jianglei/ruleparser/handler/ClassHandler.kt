package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.RuleKeyWord
import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import kotlin.math.sin

/**
 *
 * @author jianglei on 3/30/19.
 */
class ClassHandler(singleRule:String) : AbstractRuleHandler(singleRule) {
    /**
     * 通过类名正则匹配class类名
     */
    private val classNameRegxPattern = Regex("@class:regex<(.*)>(\\[(\\d)])?")
    /**
     * 通过类名完全匹配
     */
    private val classNamePattern = Regex("@class:<(.*)>(\\[(\\d)])?")

    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
    }

    override fun targetType(): Int {
        return TYPE_HTML_ELEMENT
    }


    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, classNameRegxPattern, classNamePattern)
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val res = mutableListOf<Element>()
        val ruleDesc = getRuleDesc()
        for (e in preResult as List<Element>) {
            val elements: Elements
            if (ruleDesc.regx != null) {
                elements = e.select("[class~=${ruleDesc.regx}]")
            } else {
                elements = e.getElementsByClass(ruleDesc.name)
            }
            if (elements == null || elements.size == 0) {
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