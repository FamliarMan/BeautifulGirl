package com.jianglei.ruleparser.data

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.jianglei.ruleparser.R
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URLDecoder


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 *
 */
@Suppress("OverridingDeprecatedMember")
class InVisibleWebViewFragment : Fragment() {
    private var listener: OnWebViewResultListener? = null
    private var invisibleWebView: WebView? = null
    private var url: String? = null
    private val handler = Handler(Looper.getMainLooper())
    var loadingFinished = true

    private val notifyRunnable = Runnable {
        if (loadingFinished) {
            invisibleWebView?.evaluateJavascript(
                "javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);"
                , null
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public fun reset() {
        loadingFinished = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_in_visible_web_view, container, false)
        invisibleWebView = view.findViewById(R.id.invisibleWebView)
        initView()
        if (url != null) {
            reset()
            invisibleWebView!!.loadUrl(url)
        }
        return view
    }

    public fun getHtml(url: String, listener: OnWebViewResultListener) {
        reset()
        if (invisibleWebView != null) {
            invisibleWebView!!.loadUrl(url)
        } else {
            this.url = url
        }
        this.listener = listener
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initView() {
        val webSettings = invisibleWebView!!.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true

        invisibleWebView!!.addJavascriptInterface(InJavaScriptLocalObj(), "java_obj")
        WebView.setWebContentsDebuggingEnabled(true)


        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        //禁止加载图片
        webSettings.blockNetworkImage = true


        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式

        invisibleWebView!!.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {

                super.onPageFinished(view, url)
                loadingFinished = true
                //延迟500ms回调，如果有重定向现象，500ms后会发现，将不会执行回调
                handler.postDelayed(notifyRunnable, 1000)

            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingFinished = false
                handler.removeCallbacks(notifyRunnable)

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                loadingFinished = false
                view?.loadUrl(request!!.url!!.toString())
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                loadingFinished = false
                view?.loadUrl(url)
                return false
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = if (request == null) {
                    null
                } else {
                    request.url.toString()
                }
                return intercept(url)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return intercept(url)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }



        }
        invisibleWebView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                }

            }
        }
    }

    private fun intercept(url: String?): WebResourceResponse? {

        if (url == null) {
            //交给浏览器处理
            return null
        }
        val lastIndex = url.lastIndexOf(".")
        if (lastIndex == 0 || lastIndex == -1) {
            return null
        }
        val suffix = URLDecoder.decode(url.substring(lastIndex), "utf-8")
        val exceptType = arrayOf(
            ".css", ".jpg", ".png", ".gif", ".webp",
            ".m3u8", ".mp4", ".avi", ".hls", ".ts", ".svg"
        )
        for (sf in exceptType) {
            if (suffix.contains(sf)) {
                return getResponseData()
            }
        }
        return null
    }

    private fun getResponseData(): WebResourceResponse? {
        try {
            val str = "Access Denied"
            val data = ByteArrayInputStream(str.toByteArray(charset("UTF-8")))
            return WebResourceResponse("text/css", "UTF-8", data)
        } catch (e: IOException) {
            return null
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    public fun cancel() {
//        invisibleWebView?.removeAllViews()
        invisibleWebView?.stopLoading()
    }


    inner class InJavaScriptLocalObj {
        @JavascriptInterface
        public fun showSource(html: String) {
            handler.post {
                listener?.onSuccess(html, invisibleWebView!!)
            }
        }

    }


}
