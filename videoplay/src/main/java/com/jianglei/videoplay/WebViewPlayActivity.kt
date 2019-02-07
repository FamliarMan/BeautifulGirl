package com.jianglei.videoplay

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_web_view_play.*
import java.io.ByteArrayInputStream
import java.io.IOException
import android.view.WindowManager
import android.R.attr.orientation
import android.content.res.Configuration
import android.util.Log
import android.webkit.WebChromeClient.CustomViewCallback
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.webkit.WebChromeClient


/**
 * 有些网站视频真实地址加密了，无法破解，只能直接跳转到对应web页面
 */
class WebViewPlayActivity : AppCompatActivity() {

    private var isShowProcess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_play)
        val playUrl = intent.getStringExtra("playUrl")
        val webSettings = webView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true)


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true) //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true) // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true) //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false) //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK) //关闭webview中缓存
        webSettings.setAllowFileAccess(true) //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true) //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true) //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8")//设置编码格式

        webView.webViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request!!.url!!.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                if (request == null) {
                    return null
                }
                val url = request.url.toString()
                if (url.contains("ads")
                ) {
                    return getResponseData()
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


        }
        webView.webChromeClient = object : WebChromeClient() {
            var mCallback: WebChromeClient.CustomViewCallback? = null
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                seekBar.progress = newProgress
                if (newProgress == 100) {
                    seekBar.visibility = View.GONE
                }
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                Log.d("ToVmp", "onShowCustomView")
                fullScreen()

                webView.visibility = View.GONE
                container.visibility = View.VISIBLE
                container.addView(view)
                mCallback = callback
                super.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                Log.i("ToVmp", "onHideCustomView")
                fullScreen()
                webView.visibility = View.VISIBLE
                container.visibility = View.GONE
                container.removeAllViews()
                super.onHideCustomView()
            }
        }
        webView.loadUrl(playUrl)
    }

    private fun fullScreen() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Log.i("ToVmp", "横屏")
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Log.i("ToVmp", "竖屏")
        }
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
