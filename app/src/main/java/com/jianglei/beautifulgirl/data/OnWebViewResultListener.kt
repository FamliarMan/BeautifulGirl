package com.jianglei.beautifulgirl.data

import android.webkit.WebView

/**
 * @author jianglei on 2/7/19.
 */
interface OnWebViewResultListener {
    fun onSuccess(html:String,webView:WebView){
        onSuccess(html)
    }
    fun onSuccess(html:String){}
    fun onError(code:Int,msg:String)
}