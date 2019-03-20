package com.jianglei.ruleparser

import com.google.gson.Gson
import com.google.gson.JsonParser

/**
 * @author jianglei on 3/20/19.
 */
class GsonUtil {
    companion object {
        public val gson = Gson()
        public val gsonParser = JsonParser()
    }
}