package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 *
 * @author jianglei on 3/30/19.
 */
class RegexAllHandler(singleRule: String) : AbstractRuleHandler(singleRule) {

    private val regexPattern = Regex("@regexAll:<(.*)>(\\[(\\d)])?")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT, TYPE_STRING)
    }

    override fun targetType(): Int {
        return TYPE_STRING
    }


    override fun getRuleDesc(): RuleDesc {
        val m = regexPattern.find(singleRule)
        if (m != null) {
            val nameRegx = m.groupValues[1]
            val index: Int? = if (m.groupValues[3] == "") {
                null
            } else {
                m.groupValues[3].toInt()
            }
            return RuleDesc(nameRegx, nameRegx, index)
        }
        throw IllegalSyntaxException("非法的正则规则:$singleRule")
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        val preClassType = checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val ruleDesc = getRuleDesc()
        if (ruleDesc.regx == null) {
            throw IllegalArgumentException("Wrong regex rule")
        }
        val res = mutableListOf<String>()
        val regex: Regex
        try {
            regex = Regex(ruleDesc.regx)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalArgumentException("错误的正则规则：${ruleDesc.regx}")
        }
        var preStrings: List<String>
        if (preClassType == Element::class || preClassType == Document::class) {
            preStrings = (preResult as List<Element>).map {
                it.toString()
            }.toList()
        } else {
            preStrings = preResult as List<String>
        }
        for (e in preStrings) {
            val ms = regex.findAll(e)
            ms.forEach { m ->
                if (ruleDesc.index == null) {
                    res.add(m.groupValues[0])
                } else if (ruleDesc.index >= m.groupValues.size) {
                    throw java.lang.IllegalArgumentException("正则表达式中的括号选取index越界")

                } else {
                    res.add(m.groupValues[ruleDesc.index])
                }

            }
        }
        return res
    }
}