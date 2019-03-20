package com.jianglei.ruleparser

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

/**
 * @author jianglei on 3/20/19.
 */
class JsonRuleParser {

    companion object {
        private val jsonArrRegex = Regex("^@jsonArr:<(.*)>$")
        private val jsonObjRegex = Regex("^@jsonObj:<(.*)>$")
        private val jsonValueRegex = Regex("^@jsonValue:<(.*)>$")
        private val regexAll = mutableListOf(jsonArrRegex, jsonObjRegex, jsonValueRegex)


        fun isJsonArrRule(jsonRule: String): Boolean {
            return jsonRule.trim().startsWith("@jsonArr")
        }

        fun isJsonObjRule(jsonRule: String): Boolean {
            return jsonRule.trim().startsWith("@jsonObj")
        }

        fun isJsonValueRule(jsonRule: String): Boolean {
            return jsonRule.trim().startsWith("@jsonValue")
        }

        fun getKeyName(jsonRule: String): String {
            val rule = jsonRule.trim()
            for (regex in regexAll) {
                val m = regex.find(rule)
                    ?: continue
                if (m.groupValues[1].isEmpty()) {
                    ExceptionUtils.throwIllegalArgumentException("非法的json规则：$jsonRule")
                }
                return m.groupValues[1]
            }
            ExceptionUtils.throwIllegalArgumentException("非法过滤规则$jsonRule")
        }

        fun getJsonArrFromString(jsonRule: String, preStrings: List<String>): List<JsonArray> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonArray>()
            try {
                for (preString in preStrings) {
                    val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                    val arr = jsonObject.getAsJsonArray(key)
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
                return res
            } catch (e:Throwable) {
                e.printStackTrace()
                ExceptionUtils.throwIllegalArgumentException("json转换时对象不是合法的json数据")

            }

        }

        fun getJsonArrFromObj(jsonRule: String, preObjects: List<JsonObject>): List<JsonArray> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonArray>()
            for (preObj in preObjects) {
                val arr = preObj.getAsJsonArray(key)
                    ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                res.add(arr)
            }
            return res
        }

        fun getJsonArrFromArr(jsonRule: String, preArrs: List<JsonArray>): List<JsonArray> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonArray>()
            for (preArr in preArrs) {
                for (i in 0..preArr.size()) {
                    val arr = preArr.get(i).asJsonObject.getAsJsonArray(key)
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
            }
            return res
        }

        fun getJsonObjFromString(jsonRule: String, preStrings: List<String>): List<JsonObject> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonObject>()
            try {
                for (preString in preStrings) {
                    val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                    val arr = jsonObject.getAsJsonObject(key)
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
                return res
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                ExceptionUtils.throwIllegalArgumentException("json转换时对象不是合法的json数据")

            }

        }

        fun getJsonObjFromObj(jsonRule: String, preObjects: List<JsonObject>): List<JsonObject> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonObject>()
            for (preObj in preObjects) {
                val arr = preObj.getAsJsonObject(key)
                    ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                res.add(arr)
            }
            return res
        }

        fun getJsonObjFromArr(jsonRule: String, preArrs: List<JsonArray>): List<JsonObject> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<JsonObject>()
            for (preArr in preArrs) {
                for (i in 0..preArr.size()) {
                    val arr = preArr.get(i).asJsonObject.getAsJsonObject(key)
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
            }
            return res
        }

        fun getJsonValueFromString(jsonRule: String, preStrings: List<String>): List<String> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<String>()
            try {
                for (preString in preStrings) {
                    val jsonObject = GsonUtil.gsonParser.parse(preString).asJsonObject
                    val arr = jsonObject.getAsJsonPrimitive(key).asString
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
                return res
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                ExceptionUtils.throwIllegalArgumentException("json转换时对象不是合法的json数据")

            }

        }

        fun getJsonValueFromObj(jsonRule: String, preObjects: List<JsonObject>): List<String> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<String>()
            for (preObj in preObjects) {
                val arr = preObj.getAsJsonPrimitive(key).asString
                    ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                res.add(arr)
            }
            return res
        }

        fun getJsonValueFromArr(jsonRule: String, preArrs: List<JsonArray>): List<String> {
            val key = getKeyName(jsonRule)
            val res = mutableListOf<String>()
            for (preArr in preArrs) {
                for (i in 0..preArr.size()) {
                    val arr = preArr.get(i).asJsonObject.getAsJsonPrimitive(key).asString
                        ?: ExceptionUtils.throwIllegalArgumentException("错误的json key： $key")
                    res.add(arr)
                }
            }
            return res
        }
    }
}