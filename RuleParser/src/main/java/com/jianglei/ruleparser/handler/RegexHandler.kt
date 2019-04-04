package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 *
 * @author jianglei on 3/30/19.
 */
class RegexHandler(singleRule: String) : AbstractRuleHandler(singleRule) {

    private val regexPattern = Regex("@regex:<(.*)>(\\[(\\d)])?")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_HTML_ELEMENT)
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

    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
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
        for (e in preResult as List<String>) {
            val m = regex.find(e.toString())
            if (m != null) {
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