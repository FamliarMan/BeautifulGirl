package com.jianglei.ruleparser.data

import okhttp3.Cookie

/**
 * @author jianglei on 2/5/19.
 */
class CookitCenter {
    companion object {
        private val center = hashMapOf<String,List<Cookie>>()

        fun put(url:String,cookie:List<Cookie>){
            center[url] = cookie
        }

        fun get(url:String):List<Cookie>?{
            return center[url]
        }
    }

}