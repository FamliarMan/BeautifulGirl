package com.jianglei.ruleparser

import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * @author jianglei on 3/16/19.
 */
class RuleParser(val document: Document) {
    private val elementsCache: MutableMap<String, Elements> = mutableMapOf()
    /**
     * 根据规则获取字符串结果，返回值统一为列表，便于抽象
     */
    fun getStrings(rule: String): List<String> {
        val singleRules = rule.split("->")
        var parentElements = Elements()
        var totalRule = ""
        parentElements.add(document)
        try {

            for (singleRule in singleRules) {
                if (totalRule.isEmpty()) {
                    totalRule = totalRule.plus(singleRule.trim())
                } else {
                    totalRule = totalRule.plus("->").plus(singleRule.trim())
                }
                when {
                    singleRule.trim().startsWith(RuleKeyWord.REGX) -> {
                        return getRegexStrings(SingleRuleParser.getRegexRuleDesc(singleRule), parentElements)
                    }
                    singleRule.trim().startsWith(RuleKeyWord.PROPERTY) -> {
                        return getPropertyString(SingleRuleParser.getPropertyRuleDesc(singleRule), parentElements)
                    }
                    singleRule.trim() == RuleKeyWord.TEXT -> {
                        return getText(parentElements)
                    }
                    else -> {
                        if (elementsCache.get(totalRule) != null) {
                            parentElements = elementsCache.getValue(totalRule)
                        } else {
                            parentElements = getElements(singleRule, parentElements)
                            elementsCache.put(totalRule, parentElements)
                        }
                    }
                }
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalArgumentException("非法规则描述")
        }
        throw IllegalArgumentException("应该以@property、@regex或@text规则结尾")

    }

    fun getElements(singleRule: String, parent: Elements): Elements {
        when {
            singleRule.trim().startsWith(RuleKeyWord.CLASS) -> {
                val classRule = SingleRuleParser.getClassRuleDesc(singleRule)

                return getClassElements(classRule, parent)


            }
            singleRule.trim().startsWith(RuleKeyWord.ID) -> {
                val idRule = SingleRuleParser.getIdRuleDesc(singleRule)
                return getIdlements(idRule, parent)

            }
            singleRule.trim().startsWith(RuleKeyWord.LABEL) -> {
                val labelRule = SingleRuleParser.getLabelRuleDesc(singleRule)
                return getLabellements(labelRule, parent)

            }
            else -> throw IllegalArgumentException("非法规则描述")
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
                return res
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
                elements.add(element)
            }
            if (elements.size == 0) {
                return res
            }
            if (idRule.index != null) {
                res.add(elements[idRule.index])
            } else {
                res.addAll(elements)
            }
        }
        return res

    }


    fun getLabellements(labelRule: RuleDesc, parent: Elements): Elements {
        val res = Elements()
        for (e in parent) {
            val elements = e.select(labelRule.name)
            if (elements.size == 0) {
                return res
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
            throw IllegalArgumentException("Wrong regex rule")
        }
        val res = mutableListOf<String>()
        val regex = Regex(regexRule.regx)
        for (e in parent) {
            val m = regex.find(e.toString())
            if (m != null) {
                if (regexRule.index == null) {
                    res.add(m.groupValues[0])
                } else if (regexRule.index >= m.groupValues.size) {
                    throw IllegalArgumentException("正则表达式中的括号选取index越界")

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
}