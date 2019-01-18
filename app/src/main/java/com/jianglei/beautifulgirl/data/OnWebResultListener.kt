package com.jianglei.beautifulgirl.data

/**
 * @author jianglei on 1/6/19.
 */
interface OnWebResultListener {
    fun onSuccess(html:String)
    fun onError(code:Int,msg:String)
}