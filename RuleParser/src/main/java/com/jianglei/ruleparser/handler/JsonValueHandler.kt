package com.jianglei.ruleparser.handler

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jianglei.ruleparser.GsonUtil
import com.jianglei.ruleparser.ruledescription.RuleDesc

/**
 * @author jianglei on 4/4/19.
 */
class JsonValueHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    private val jsonValueRegex = Regex("^@jsonValue:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(
            TYPE_STRING, TYPE_JSON_OBJ, TYPE_JSON_ARR
        )
    }

    override fun targetType(): Int {
        return TYPE_STRING
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        val lastClassType = checkAndReturnClassTyepe(lastRuleHandler, preResult)
        return when (lastClassType) {
            String::class -> getJsonValueFromString(singleRule, preResult as List<String>)
            JsonObject::class -> getJsonValueFromObj(singleRule, preResult as List<JsonObject>)
            else -> getJsonValueFromArr(singleRule, preResult as List<JsonArray>)
        }

    }


    fun getJsonValueFromString(jsonRule: String, preStrings: List<String>): List<String> {
        val key = getRuleDesc().name
        val res = mutableListOf<String>()
        try {
            for (preString in preStrings) {
                val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                val arr = jsonObject.getAsJsonPrimitive(key).asString
                    ?: throw IllegalSyntaxException("错误的json key： $key")
                if (arr.isBlank()) {
                    continue
                }
                res.add(arr)
            }
            return res
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalSyntaxException("非法的json规则:$jsonRule")

        }

    }

    fun getJsonValueFromObj(jsonRule: String, preObjects: List<JsonObject>): List<String> {
        val key = getRuleDesc().name
        val res = mutableListOf<String>()
        try {
            for (preObj in preObjects) {
                val arr = preObj.getAsJsonPrimitive(key).asString
                    ?: throw IllegalSyntaxException("错误的json key： $key")
                if (arr.isBlank()) {
                    continue
                }
                res.add(arr)
            }
            return res
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalSyntaxException("非法json规则:$jsonRule")
        }
    }

    fun getJsonValueFromArr(jsonRule: String, preArrs: List<JsonArray>): List<String> {
        val key = getRuleDesc().name
        val res = mutableListOf<String>()
        try {
            for (preArr in preArrs) {
                for (i in 0 until preArr.size()) {
                    if (preArr[i].isJsonPrimitive) {
                        res.add(preArr[i].asJsonPrimitive.asString)
                    } else {
                        val arr = preArr.get(i).asJsonObject.getAsJsonPrimitive(key).asString
                            ?: throw IllegalSyntaxException("错误的json key： $key")
                        if (arr.isBlank()) {
                            continue
                        }
                        res.add(arr)
                    }
                }
            }
            return res
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalSyntaxException("非法的json规则：$jsonRule")
        }
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, jsonValueRegex)
    }
}