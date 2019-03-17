package com.jianglei.ruleparser

import com.jianglei.ruleparser.ruledescription.RuleDesc


/**
 * @author jianglei on 3/16/19.
 */
class SingleRuleParser {

    companion object {
        /**
         * 通过类名正则匹配class类名
         */
        private val classNameRegxPattern = Regex("@class:regex<(.*)>(\\[(\\d)])?")
        /**
         * 通过类名完全匹配
         */
        private val classNamePattern = Regex("@class:<(.*)>(\\[(\\d)])?")

        /**
         * 通过类名正则匹配class类名
         */
        private val idRegxPattern = Regex("@id:regex<(.*)>(\\[(\\d)])?")
        /**
         * 通过id名完全匹配
         */
        private val idPattern = Regex("@id:<(.*)>(\\[(\\d)])?")

        /**
         * 通过标签匹配
         */
        private val labelPattern = Regex("@label:<(.*)>(\\[(\\d)])?")

        /**
         * 通过正则表达式匹配
         */
        private val regexPattern = Regex("@regex:<(.*)>(\\[(\\d)])?")

        private val propertyPattern = Regex("@property:<(.*)>$")

        /**
         * 获取类元素描述规则
         */
        fun getClassRuleDesc(rule: String): RuleDesc {
            return getRuleDesc(rule, classNameRegxPattern, classNamePattern)
        }

        /**
         * 获取id元素的描述规则
         */
        fun getIdRuleDesc(rule: String): RuleDesc {
            return getRuleDesc(rule, idRegxPattern, idPattern)
        }

        fun getLabelRuleDesc(rule: String): RuleDesc {
            return getRuleDesc(rule, null, labelPattern)
        }

        fun getPropertyRuleDesc(rule: String): RuleDesc {
            return getRuleDesc(rule, null, propertyPattern)
        }
        fun getRegexRuleDesc(rule: String): RuleDesc {
            val m = regexPattern.find(rule)
            if (m != null) {
                val nameRegx = m.groupValues[1]
                val index: Int? = if (m.groupValues[3] == "") {
                    null
                } else {
                    m.groupValues[3].toInt()
                }
                return RuleDesc(null, nameRegx, index)
            }
            throw IllegalArgumentException("不合法的规则描述")
        }


        private fun getRuleDesc(
            rule: String, nameRegxPattern: Regex?,
            namePattern: Regex
        ): RuleDesc {

            var m = nameRegxPattern?.find(rule)
            if (m != null) {
                val nameRegx = m.groupValues[1]
                val index: Int? = if (m.groupValues[3] == "") {
                    null
                } else {
                    m.groupValues[3].toInt()
                }
                return RuleDesc(null, nameRegx, index)
            }
            m = namePattern.find(rule)
            if (m != null) {
                //使用类名完全匹配类名

                val name = m.groupValues[1]
                val index: Int? = if (m.groupValues.size < 4 || m.groupValues[3] == "") {
                    null
                } else {
                    m.groupValues[3].toInt()
                }
                return RuleDesc(name, null, index)
            }

            throw IllegalArgumentException("不合法的规则描述")
        }

    }
}