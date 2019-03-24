package com.jianglei.beautifulgirl.data

import android.util.Log
import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import com.elvishew.xlog.LogUtils
import com.elvishew.xlog.XLog
import com.jianglei.ruleparser.LogUtil

/**
 * 动态网站获取工具
 * @author jianglei on 2/6/19.
 */
class DynamicWebGetter {
    private var isCanceled = false
    private var fragment:InVisibleWebViewFragment? = null

    public fun reset(){
        fragment?.reset()
        isCanceled = false
    }
    public fun getWebHtml(
        activity: FragmentActivity,
        url: String,
        listener: OnWebViewResultListener
    ) {
        isCanceled = false
        reset()
        val fm = activity.supportFragmentManager
        fragment = fm.findFragmentByTag("webview") as InVisibleWebViewFragment?
        if (fragment == null) {
            fragment = InVisibleWebViewFragment()
            fm.beginTransaction().add(fragment!!, "webview").commit()
        }
        (fragment as InVisibleWebViewFragment).getHtml(url, object : OnWebViewResultListener {
            override fun onSuccess(html: String,webView: WebView) {
                if (isCanceled) {
                    return
                }
                if(LogUtil.logHtml){
                    LogUtil.d(html)
                }
                listener.onSuccess(html,webView)
            }


            override fun onError(code: Int, msg: String) {
                if (isCanceled) {
                    return
                }
                listener.onError(code, msg)
            }
        })
    }

    public fun cancel() {
        isCanceled = true
        fragment?.cancel()
    }

}