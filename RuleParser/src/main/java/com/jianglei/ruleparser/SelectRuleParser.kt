package com.jianglei.ruleparser

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.select.Elements


/**
 * @author jianglei on 3/16/19.
 */
class SelectRuleParser {

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
         * 判断[selectRule]是否是一个元素提取规则
         */
        fun isElementSelectRule(selectRule: String): Boolean {
            val rule = selectRule.trim()
            return rule.startsWith(RuleKeyWord.CLASS)
                    || rule.startsWith(RuleKeyWord.ID)
                    || rule.startsWith(RuleKeyWord.LABEL)
        }

        /**
         * 判断[selectRule] 是否是一个字符串提取规则
         */
        fun isStringSelectRule(selectRule: String): Boolean {
            val rule = selectRule.trim()
            return rule.startsWith(RuleKeyWord.PROPERTY)
                    || rule.startsWith(RuleKeyWord.TEXT)
                    || rule.startsWith(RuleKeyWord.REGX)
        }

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
                return RuleDesc(nameRegx, nameRegx, index)
            }
            ExceptionUtils.throwIllegalArgumentException("非法的正则规则:$rule")
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

            ExceptionUtils.throwIllegalArgumentException("不合法的规则描述:$rule")
        }


        fun getSelectElements(singleRule: String, parent: Elements): Elements {
            when {
                singleRule.trim().startsWith(RuleKeyWord.CLASS) -> {
                    val classRule = SelectRuleParser.getClassRuleDesc(singleRule)

                    return getClassElements(classRule, parent)


                }
                singleRule.trim().startsWith(RuleKeyWord.ID) -> {
                    val idRule = SelectRuleParser.getIdRuleDesc(singleRule)
                    return getIdlements(idRule, parent)

                }
                singleRule.trim().startsWith(RuleKeyWord.LABEL) -> {
                    val labelRule = SelectRuleParser.getLabelRuleDesc(singleRule)
                    return getLabelElements(labelRule, parent)

                }
                else -> ExceptionUtils.throwIllegalArgumentException("非法规则描述")
            }
        }

        fun getClassElements(classRule: RuleDesc, parent: Elements): Elements {
            val res = Elements()
            for (e in parent) {
                val elements: Elements
                if (classRule.regx != null) {
                    elements = e.select("[class~=${classRule.regx}]")
                } else {
                    elements = e.getElementsByClass(classRule.name)
                }
                if (elements == null || elements.size == 0) {
                    continue
                }
                if (classRule.index != null) {
                    res.add(elements[classRule.index])
                } else {
                    res.addAll(elements)
                }
            }
            return res

        }


        fun getIdlements(idRule: RuleDesc, parent: Elements): Elements {
            val res = Elements()
            for (e in parent) {
                val elements: Elements
                if (idRule.regx != null) {
                    elements = e.select("[id~=${idRule.regx}]")
                } else {
                    val element = e.getElementById(idRule.name)
                    elements = Elements()
                    if(element != null) {
                        elements.add(element)
                    }
                }
                if (elements.size == 0) {
                    continue
                }

                if (idRule.index != null) {
                    res.add(elements[idRule.index])
                } else {
                    res.addAll(elements)
                }
            }
            return res

        }


        fun getLabelElements(labelRule: RuleDesc, parent: Elements): Elements {
            val res = Elements()
            for (e in parent) {
                val elements = e.select(labelRule.name)
                if (elements.size == 0) {
                    continue
                }
                if (labelRule.index != null) {
                    res.add(elements[labelRule.index])
                } else {
                    res.addAll(elements)
                }
            }
            return res

        }

        fun getRegexStrings(regexRule: RuleDesc, parent: Elements): List<String> {
            if (regexRule.regx == null) {
                ExceptionUtils.throwIllegalArgumentException("Wrong regex rule")
            }
            val res = mutableListOf<String>()
            val regex: Regex
            try {
                regex = Regex(regexRule.regx)
            } catch (e: Throwable) {
                e.printStackTrace()
                ExceptionUtils.throwIllegalArgumentException("错误的正则规则：${regexRule.regx}")
            }
            for (e in parent) {
                val m = regex.find(e.toString())
                if (m != null) {
                    if (regexRule.index == null) {
                        res.add(m.groupValues[0])
                    } else if (regexRule.index >= m.groupValues.size) {
                        ExceptionUtils.throwIllegalArgumentException("正则表达式中的括号选取index越界")

                    } else {
                        res.add(m.groupValues[regexRule.index])
                    }

                }
            }
            return res
        }

        fun getPropertyString(propertyRule: RuleDesc, parent: Elements): List<String> {
            val res = mutableListOf<String>()
            for (e in parent) {
                res.add(e.attr(propertyRule.name))
            }
            return res
        }

        fun getText(parent: Elements): List<String> {
            val res = mutableListOf<String>()
            for (e in parent) {
                res.add(e.text())
            }
            return res
        }

        fun getSelectStrings(selectRule: String, parent: Elements): List<String> {
            var ruleDesc: RuleDesc
            val rule = selectRule.trim()
            when {

                rule.startsWith(RuleKeyWord.REGX) -> {
                    ruleDesc = SelectRuleParser.getRegexRuleDesc(rule)
                    return getRegexStrings(ruleDesc, parent)
                            as MutableList<String>
                }
                rule.startsWith(RuleKeyWord.PROPERTY) -> {
                    ruleDesc = SelectRuleParser.getPropertyRuleDesc(rule)
                    return getPropertyString(ruleDesc, parent)
                            as MutableList<String>
                }
                rule == RuleKeyWord.TEXT -> {
                    return getText(parent) as MutableList<String>
                }
                else -> {
                    ExceptionUtils.throwIllegalArgumentException("非法的过滤规则$rule")
                }

            }
        }

    }
}