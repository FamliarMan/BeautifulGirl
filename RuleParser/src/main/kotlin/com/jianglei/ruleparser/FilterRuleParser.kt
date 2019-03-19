package com.jianglei.ruleparser

/**
 * @author jianglei on 3/17/19.
 */
class FilterRuleParser {
    companion object {
        val hasClassRegex = Regex("@hasClass<(.*)>")
        val noClassRegex = Regex("@noClass<(.*)>")
        val hasIdRegex = Regex("@hasId<(.*)>")
        val noIdRegex = Regex("@noId<(.*)>")
        val hasLabelRegex = Regex("@hasLabel<(.*)>")
        val noLabelRegex = Regex("@noLabel<(.*)>")
        val equalsRegex = Regex("@==<(.*)>")
        val notEqualsRegex = Regex("@!=<(.*)>")
        val regexArray = listOf(
            hasClassRegex, noClassRegex, hasIdRegex,
            noIdRegex, hasLabelRegex, noLabelRegex, equalsRegex, notEqualsRegex
        )


        /**
         * 获取过滤规则中的值，@noLabel<label1> 中的label1,如果返回null，
         * 说明[filterRule] 不是合法的过滤规则
         */
        fun getConditionValue(filterRule: String): List<String>? {
            val res = mutableListOf<String>()
            for (regex in regexArray) {
                val m = regex.find(filterRule) ?: continue
                if (m.groupValues[1].isEmpty()) {
                    return null
                }
                res.addAll(m.groupValues[1].split(","))
            }
            return if (res.isEmpty()) {
                null
            } else {
                res
            }
        }
    }

}