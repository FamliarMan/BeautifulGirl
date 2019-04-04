package com.jianglei.ruleparser.handler

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jianglei.ruleparser.GsonUtil
import com.jianglei.ruleparser.ruledescription.RuleDesc

/**
 * @author jianglei on 4/4/19.
 */
class JsonArrHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    private val jsonArrRegex = Regex("^@jsonArr:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(
            TYPE_STRING, TYPE_JSON_OBJ
        )
    }

    override fun targetType(): Int {
        return TYPE_JSON_ARR
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        val lastClassType = checkAndReturnClassTyepe(lastRuleHandler, preResult)
        if (lastClassType == String::class) {
            return getJsonArrFromString(singleRule, preResult as List<String>)
        } else {
            return getJsonArrFromObj(singleRule, preResult as List<JsonObject>)
        }

    }

    private fun getJsonArrFromString(jsonRule: String, preStrings: List<String>): List<JsonArray> {
        val key = getRuleDesc().name
        val res = mutableListOf<JsonArray>()
        try {
            for (preString in preStrings) {
                val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                val arr = jsonObject.getAsJsonArray(key)
                    ?: throw IllegalStateException("错误的json key： $key")
                res.add(arr)
            }
            return res
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalSyntaxException("json转换时对象不是合法的json数据")

        }

    }

    private fun getJsonArrFromObj(jsonRule: String, preObjects: List<JsonObject>): List<JsonArray> {
        val key = getRuleDesc().name
        val res = mutableListOf<JsonArray>()
        for (preObj in preObjects) {
            val arr = preObj.getAsJsonArray(key)
                ?: throw IllegalSyntaxException("错误的json key： $key")
            res.add(arr)
        }
        return res
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, jsonArrRegex)
    }
}