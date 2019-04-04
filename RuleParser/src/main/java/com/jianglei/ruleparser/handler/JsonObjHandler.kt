package com.jianglei.ruleparser.handler

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jianglei.ruleparser.GsonUtil
import com.jianglei.ruleparser.getAvaliableUri
import com.jianglei.ruleparser.ruledescription.RuleDesc

/**
 * @author jianglei on 4/4/19.
 */
class JsonObjHandler(singleRule: String) : AbstractRuleHandler(singleRule) {
    private val jsonObjRegex = Regex("^@jsonObj:<(.*)>$")
    override fun acceptType(): List<Int> {
        return listOf(
            TYPE_STRING, TYPE_JSON_OBJ, TYPE_JSON_ARR
        )
    }

    override fun targetType(): Int {
        return TYPE_JSON_OBJ
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any> {
        val lastClassType = checkAndReturnClassTyepe(lastRuleHandler, preResult)
        if (lastClassType == String::class) {
            return getJsonObjFromString(singleRule, preResult as List<String>)
        } else if (lastClassType == JsonObject::class) {
            return getJsonObjFromObj(singleRule, preResult as List<JsonObject>)
        } else {
            return getJsonObjFromArr(singleRule, preResult as List<JsonArray>)
        }

    }


    fun getJsonObjFromString(jsonRule: String, preStrings: List<String>): List<JsonObject> {
        val key = getRuleDesc().name
        val res = mutableListOf<JsonObject>()
        try {
            for (preString in preStrings) {
                val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                val arr = jsonObject.getAsJsonObject(key)
                    ?: throw IllegalSyntaxException("错误的json key： $key")
                res.add(arr)
            }
            return res
        } catch (e: Throwable) {
            e.printStackTrace()
            throw IllegalSyntaxException("错误的json规则或json数据")

        }

    }

    fun getJsonObjFromObj(jsonRule: String, preObjects: List<JsonObject>): List<JsonObject> {
        val key = getRuleDesc().name
        val res = mutableListOf<JsonObject>()
        for (preObj in preObjects) {
            val arr = preObj.getAsJsonObject(key)
                ?: throw IllegalSyntaxException("错误的json key： $key")
            res.add(arr)
        }
        return res
    }

    fun getJsonObjFromArr(jsonRule: String, preArrs: List<JsonArray>): List<JsonObject> {
        val key = getRuleDesc().name
        val res = mutableListOf<JsonObject>()
        for (preArr in preArrs) {
            for (i in 0 until preArr.size()) {
                val arr = preArr.get(i).asJsonObject.getAsJsonObject(key)
                    ?: throw IllegalSyntaxException("错误的json key： $key")
                res.add(arr)
            }
        }
        return res
    }

    override fun getRuleDesc(): RuleDesc {
        return getRuleDesc(singleRule, null, jsonObjRegex)
    }
}