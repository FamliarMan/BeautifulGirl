package com.jianglei.ruleparser

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * @author jianglei on 3/16/19.
 */
class HtmlParser(private val document: Document) {
    private val elementsCache: MutableMap<String, Elements> = mutableMapOf()
    private val jsonCache: MutableMap<String, List<Any>> = mutableMapOf()
    /**
     * 根据规则获取字符串结果，返回值统一为列表，便于抽象
     */
    fun getStrings(rule: String): List<String> {
        val singleRules = rule.split("->")
        //上一个规则得到的元素结果
        var preElements = Elements()
        var totalRule = ""
        var preStrings: List<String>? = null
        var preStringRule: String? = null
        var preElementRule: String? = null
        var preJsonArr: List<JsonArray>? = null
        var preJsonObj: List<JsonObject>? = null
        var preJsonValue: List<String>? = null
        var preJsonRule: String? = null
        preElements.add(document)
        try {

            for (singleRule in singleRules) {
                totalRule = if (totalRule.isEmpty()) {
                    totalRule.plus(singleRule.trim())
                } else {
                    totalRule.plus("->").plus(singleRule.trim())
                }
                when {

                    SelectRuleParser.isElementSelectRule(singleRule) -> {
                        //进入到元素提取过程
                        if (preStringRule != null) {
                            ExceptionUtils.throwIllegalArgumentException("$singleRule 不能跟在$preStringRule 后面")
                        }
                        if (elementsCache[totalRule] != null) {
                            preElements = elementsCache.getValue(totalRule)
                        } else {
                            preElements = SelectRuleParser.getSelectElements(singleRule, preElements)
                            elementsCache[totalRule] = preElements
                        }
                        preElementRule = singleRule
                        LogUtil.d("$totalRule 规则解析结果数量:${preElements.size}")
                    }
                    FilterRuleParser.isElementFilterRule(singleRule) -> {
                        //当前进入过滤元素过程
                        if (preElementRule == null) {
                            ExceptionUtils.throwIllegalArgumentException("‘$singleRule’必须跟在元素提取规则后面")
                        }
                        if (preStringRule != null) {
                            ExceptionUtils.throwIllegalArgumentException("‘$singleRule’不能跟在$preStringRule 后面")
                        }
                        preElements = FilterRuleParser.getFilterElements(preElements, singleRule)
                        preElementRule = singleRule
                        LogUtil.d("$totalRule 规则解析结果数量:${preElements.size}")
                    }
                    FilterRuleParser.isStringFilterRule(singleRule) -> {
                        //当前进入字符串过滤过程
                        if (preStrings == null) {
                            //按照常理，这个分支不会被执行，这里只是保险
                            ExceptionUtils.throwIllegalArgumentException("字符串过滤规‘$singleRule’则必须跟在字符串提取规则后面")
                        }
                        preStrings = FilterRuleParser.getFilterStrings(preStrings, singleRule)
                        preStringRule = singleRule
                        LogUtil.d("$totalRule 规则解析结果数量:${preStrings.size}")
                    }
                    SelectRuleParser.isStringSelectRule(singleRule) -> {
                        //进入到字符串提取过程
                        if (preStringRule != null) {
                            ExceptionUtils.throwIllegalArgumentException("$singleRule 不能跟在$preStringRule 后面")
                        }
                        preStrings = SelectRuleParser.getSelectStrings(singleRule, preElements)
                        preStringRule = singleRule
                        LogUtil.d("$totalRule 规则解析结果数量:${preStrings.size}")
                    }
                    JsonRuleParser.isJsonArrRule(singleRule) -> {
                        //@jsonArr
                        if (preJsonRule == null && preStringRule == null) {
                            ExceptionUtils.throwIllegalArgumentException(
                                "$singleRule 必须跟在某个字符串规则后面"
                            )
                        }
                        if (jsonCache.containsKey(totalRule)) {
                            preJsonArr = jsonCache[totalRule] as List<JsonArray>?
                        } else {
                            when {
                                preJsonRule == null -> preJsonArr =
                                    JsonRuleParser.getJsonArrFromString(singleRule, preStrings!!)
                                JsonRuleParser.isJsonArrRule(preJsonRule) -> //上一个结果是数组
                                    ExceptionUtils.throwIllegalArgumentException("$singleRule 不能跟在$preJsonRule 后面")
                                JsonRuleParser.isJsonObjRule(preJsonRule) -> preJsonArr =
                                    JsonRuleParser.getJsonArrFromObj(singleRule, preJsonObj!!)
                            }
                            jsonCache[totalRule] = preJsonArr as List<JsonElement>
                        }

                        preJsonRule = singleRule
                        //经过json转换后preStrings应该清空
                        preStrings = null
                        LogUtil.d("$totalRule 规则解析结果数量:${preJsonArr!!.size}")
                    }
                    JsonRuleParser.isJsonObjRule(singleRule) -> {
                        //@jsonObj
                        if (preJsonRule == null && preStringRule == null) {
                            ExceptionUtils.throwIllegalArgumentException("$singleRule 不能跟在$preElementRule 后面")
                        }

                        if (jsonCache.containsKey(totalRule)) {
                            preJsonObj = jsonCache[totalRule] as List<JsonObject>?
                        } else {
                            when {
                                preJsonRule == null -> preJsonObj =
                                    JsonRuleParser.getJsonObjFromString(singleRule, preStrings!!)
                                JsonRuleParser.isJsonArrRule(preJsonRule) -> //上一个结果是数组
                                    preJsonObj = JsonRuleParser.getJsonObjFromArr(singleRule, preJsonArr!!)
                                JsonRuleParser.isJsonObjRule(preJsonRule) -> preJsonObj =
                                    JsonRuleParser.getJsonObjFromObj(singleRule, preJsonObj!!)
                            }
                            jsonCache[totalRule] = preJsonObj as List<JsonObject>
                        }
                        preJsonRule = singleRule
                        //经过json转换后preStrings应该清空
                        preStrings = null
                        LogUtil.d("$totalRule 规则解析结果数量:${preJsonObj!!.size}")

                    }
                    JsonRuleParser.isJsonValueRule(singleRule) -> {
                        //@jsonValue
                        if (preJsonRule == null && preStringRule == null) {
                            ExceptionUtils.throwIllegalArgumentException("$singleRule 不能跟在$preElementRule 后面")
                        }

                        if (jsonCache.containsKey(totalRule)) {
                            preJsonValue = jsonCache[totalRule] as List<String>?
                        } else {
                            when {
                                preJsonRule == null -> {
                                    preJsonValue =
                                        JsonRuleParser.getJsonValueFromString(singleRule, preStrings!!)
                                }
                                JsonRuleParser.isJsonArrRule(preJsonRule) -> {//上一个结果是数组
                                    preJsonValue = JsonRuleParser.getJsonValueFromArr(singleRule, preJsonArr!!)
                                }
                                JsonRuleParser.isJsonObjRule(preJsonRule) -> {
                                    preJsonValue =
                                        JsonRuleParser.getJsonValueFromObj(singleRule, preJsonObj!!)
                                }
                            }
                            jsonCache[totalRule] = preJsonValue as List<String>
                        }
                        preStrings = preJsonValue
                        preJsonRule = singleRule
                        LogUtil.d("$totalRule 规则解析结果数量:${preJsonValue!!.size}")
                    }
                    else -> {
                        ExceptionUtils.throwIllegalArgumentException("非法规则描述符：$singleRule")
                    }
                }
            }

            if (preStrings == null) {
                ExceptionUtils.throwIllegalArgumentException("不合适的规则组合：$rule,规则必须要能得出字符串结果")
            }
            return preStrings

        } catch (e: Throwable) {
            e.printStackTrace()
            if (e is IllegalArgumentException) {
                ExceptionUtils.throwIllegalArgumentException(e.toString())
            } else {
                ExceptionUtils.throwIllegalArgumentException("非法规则:$rule")
            }
        }

    }

}