package com.jianglei.ruleparser.data

/**
 * @author jianglei on 1/6/19.
 */
interface OnWebResultListener {
    fun onSuccess(html:String)
    fun onError(code:Int,msg:String)
}