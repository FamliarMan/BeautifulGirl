package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.ruledescription.RuleDesc

/**
 * @author jianglei on 4/4/19.
 */
class EqualTextHandler(val rule: String) : AbstractRuleHandler(rule) {
    private val equalsRegex = Regex("^@==:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(TYPE_STRING)
    }

    override fun targetType(): Int {
        return TYPE_STRING
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        checkAndReturnClassTyepe(lastRuleHandler, preResult)
        val filterCondition = getRuleDesc().name!!.split(",")
        return (preResult as List<String>).filter {
            var hasOneSame = false
            for (str in filterCondition) {
                if (str == it) {
                    hasOneSame = true
                    break
                }
            }
            hasOneSame
        }.toList()
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(rule, null, equalsRegex)
    }
}