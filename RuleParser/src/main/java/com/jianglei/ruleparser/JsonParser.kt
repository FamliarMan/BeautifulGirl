package com.jianglei.ruleparser

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * @author jianglei on 3/24/19.
 */
class JsonParser(private var json: String) {
    private val jsonCache: MutableMap<String, List<Any>> = mutableMapOf()
    fun getStrings(rule: String, json: String): List<String> {
        val singleRules = rule.split("->")
        //上一个规则得到的元素结果
        var totalRule = ""
        var preStrings: List<String>? = mutableListOf(json)
        var preJsonArr: List<JsonArray>? = null
        var preJsonObj: List<JsonObject>? = null
        var preJsonValue: List<String>? = null
        var preJsonRule: String? = null
        try {

            for (singleRule in singleRules) {
                totalRule = if (totalRule.isEmpty()) {
                    totalRule.plus(singleRule.trim())
                } else {
                    totalRule.plus("->").plus(singleRule.trim())
                }
                when {

                    JsonRuleParser.isJsonArrRule(singleRule) -> {
                        //@jsonArr
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