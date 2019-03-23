package com.jianglei.ruleparser

import org.jsoup.select.Elements

/**
 * @author jianglei on 3/17/19.
 */
class FilterRuleParser {
    companion object {
        private val hasClassRegex = Regex("^@hasClass:<(.*)>$")
        private val noClassRegex = Regex("^@noClass:<(.*)>$")
        private val hasIdRegex = Regex("^@hasId:<(.*)>$")
        private val noIdRegex = Regex("^@noId:<(.*)>$")
        private val hasLabelRegex = Regex("^@hasLabel:<(.*)>$")
        private val noLabelRegex = Regex("^@noLabel:<(.*)>$")
        private val equalsRegex = Regex("^@==:<(.*)>$")
        private val hasTextRegex = Regex("^@hasText:<(.*)>$")
        private val notEqualsRegex = Regex("^@!=:<(.*)>$")
        private val regexArray = listOf(
            hasClassRegex, noClassRegex, hasIdRegex,
            noIdRegex, hasLabelRegex, noLabelRegex, equalsRegex, notEqualsRegex, hasTextRegex
        )


        /**
         * 判断当前规则是否是用来过滤html元素的
         */
        fun isElementFilterRule(filterRule: String): Boolean {
            val rule = filterRule.trim()

            return rule.startsWith(RuleKeyWord.HAS_CLASS)
                    || rule.startsWith(RuleKeyWord.HAS_ID)
                    || rule.startsWith(RuleKeyWord.HAS_LABEL)
                    || rule.startsWith(RuleKeyWord.NO_LABEL)
                    || rule.startsWith(RuleKeyWord.NO_CLASS)
                    || rule.startsWith(RuleKeyWord.NO_ID)
                    || rule.startsWith(RuleKeyWord.HAS_TEXT)
        }

        /**
         * 判断当前规则是否用来过滤字符串
         */
        fun isStringFilterRule(filterRule: String): Boolean {
            val rule = filterRule.trim()

            return rule.startsWith(RuleKeyWord.EQUALS)
                    || rule.startsWith(RuleKeyWord.NOT_EQUAL)
        }

        /**
         * 获取过滤规则中的值，@noLabel<label1> 中的label1,如果返回null，
         * 说明[filterRule] 不是合法的过滤规则
         */
        fun getConditionValue(filterRule: String): List<String> {
            val res = mutableListOf<String>()
            for (regex in regexArray) {
                val m = regex.find(filterRule) ?: continue
                if (m.groupValues[1].isEmpty()) {
                    ExceptionUtils.throwIllegalArgumentException("非法过滤规则：$filterRule")
                }
                res.addAll(m.groupValues[1].split(","))
            }
            return if (res.isEmpty()) {
                ExceptionUtils.throwIllegalArgumentException("非法过滤规则：$filterRule")
            } else {
                res
            }
        }


        /**
         * 对Elements结果做过滤，[preElements]是需要过滤的对象
         * [filterRule]具体的过滤规则
         * 返回过滤后的结果
         */
        fun getFilterElements(preElements: Elements, filterRule: String): Elements {
            val rule = filterRule.trim()
            val filterCondition = FilterRuleParser.getConditionValue(rule)
            when {
                rule.startsWith(RuleKeyWord.HAS_CLASS) -> {
                    val res = preElements.filter {
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
                rule.startsWith(RuleKeyWord.NO_CLASS) -> {
                    val res = preElements.filter {
                        var notHaveAllClass = true
                        for (className in filterCondition) {
                            if (!it.getElementsByClass(className).isEmpty()) {
                                notHaveAllClass = false
                                break
                            }
                        }
                        notHaveAllClass
                    }.toList()
                    return Elements(res)
                }
                rule.startsWith(RuleKeyWord.HAS_ID) -> {
                    val res = preElements.filter {
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
                rule.startsWith(RuleKeyWord.NO_ID) -> {
                    val res = preElements.filter {
                        var notHaveAllId = true
                        for (id in filterCondition) {
                            if (it.getElementById(id) != null) {
                                notHaveAllId = false
                                break
                            }
                        }
                        notHaveAllId
                    }.toList()
                    return Elements(res)
                }
                rule.startsWith(RuleKeyWord.HAS_LABEL) -> {
                    val res = preElements.filter {
                        var hasAllLabel = true
                        for (label in filterCondition) {
                            if (it.getElementsByTag(label).isEmpty()) {
                                hasAllLabel = false
                                break
                            }
                        }
                        hasAllLabel
                    }.toList()
                    return Elements(res)
                }
                rule.startsWith(RuleKeyWord.NO_LABEL) -> {
                    val res = preElements.filter {
                        var notHaveAllLabel = true
                        for (label in filterCondition) {
                            if (it.getElementsByTag(label) != null) {
                                notHaveAllLabel = false
                                break
                            }
                        }
                        notHaveAllLabel
                    }.toList()
                    return Elements(res)
                }

                rule.startsWith(RuleKeyWord.HAS_TEXT) -> {
                    val res = preElements.filter {
                        var hasText = false
                        for (text in filterCondition) {
                            if (it.text() == text) {
                                hasText = true
                                break
                            }
                        }
                        hasText
                    }.toList()
                    return Elements(res)
                }
                else -> {
                    ExceptionUtils.throwIllegalArgumentException("非法的过滤规则")
                }
            }
        }

        fun getFilterStrings(preStrings: List<String>, filterRule: String): List<String> {
            val rule = filterRule.trim()
            val filterCondition = FilterRuleParser.getConditionValue(rule)

            when {
                rule.startsWith(RuleKeyWord.EQUALS) -> {
                    return preStrings.filter {
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
                rule.startsWith(RuleKeyWord.NOT_EQUAL) -> {
                    return preStrings.filter {
                        var hasOneSame = false
                        for (str in filterCondition) {
                            if (str == it) {
                                hasOneSame = true
                                break
                            }
                        }
                        !hasOneSame
                    }.toList()

                }
                else -> {
                    ExceptionUtils.throwIllegalArgumentException("非法的过滤规则")
                }
            }
        }

    }

}