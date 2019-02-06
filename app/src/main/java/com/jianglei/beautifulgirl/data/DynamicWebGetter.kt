package com.jianglei.beautifulgirl.data

import android.os.Handler
import android.os.Looper
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import java.util.concurrent.Executors

/**
 * 动态网站获取工具
 * @author jianglei on 2/6/19.
 */
class DynamicWebGetter {
    companion object {
        private val webClient = WebClient()
        private val executorService = Executors.newFixedThreadPool(1)
        private val handler = Handler(Looper.getMainLooper())
        private var isInit = false
        private var isCanceled = false

        private fun initClient() {
            if (isInit) {
                return
            }
            webClient.options.isCssEnabled = false
            webClient.options.isJavaScriptEnabled = true
            webClient.webConnection = OkHttpWebConnection(webClient)
            isInit = true
        }


        public fun getWebHtml(url: String,condition: Condition<String>, listener: OnWebResultListener) {
            initClient()
            isCanceled = true
            executorService.execute {
                val page: HtmlPage = webClient.getPage(url)
                var html:String? = null
                for(i in 0..10){
                    html = page.asText()
                    if(condition.isValid(html)){
                        break
                    }else{
                        Thread.sleep(2000)
                    }
                }
                if (isCanceled) {
                    webClient.close()
                    return@execute
                }
                handler.post {
                    if (html == null || html.isEmpty() ) {
                        listener.onError(404, "获取网页失败")
                    } else {
                        listener.onSuccess(html)
                    }
                }
                webClient.close()
            }

        }

        public fun cancel() {
            isCanceled = false
        }
    }

}