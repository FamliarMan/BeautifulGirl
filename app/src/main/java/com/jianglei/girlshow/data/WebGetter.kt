package com.jianglei.girlshow.data

import androidx.fragment.app.FragmentActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.Charset

/**
 * @author jianglei on 3/17/19.
 */
class WebGetter {

    private var currentCall:Call<ResponseBody>? = null
    private var dynamicWebGetter:DynamicWebGetter? = null
    fun getWebsiteHtml(
        activity: FragmentActivity,
        isDynamic: Boolean,
        url: String,
        headers: Map<String, String>,
        listener: OnWebViewResultListener,
        charset: String = "utf-8"
    ) {
        if (isDynamic) {
            dynamicWebGetter = DynamicWebGetter()
            dynamicWebGetter!!.getWebHtml(activity, url,listener)
        }else{

            currentCall = RetrofitManager.retrofit.create(WebService::class.java)
                .fetchHtmlFromWebsite(url,headers)
            currentCall!!.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onError(-1, t.localizedMessage)
                    currentCall = null

                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when {
                        response.code() == 404 -> {
                            listener.onError(404, "No data")
                        }
                        response.isSuccessful -> {
                            if (response.body() == null) {
                                listener.onError(-1, "Network Error")
                            }
                            val bytes = response.body()!!.bytes()
                            val res = String(bytes, Charset.forName(charset))
                            listener.onSuccess(res)
                        }
                        else -> {
                            listener.onError(response.code(), "Network Error")
                        }

                    }
                    response.body()?.close()
                    currentCall = null
                }

            })
        }

    }
    fun cancel(){
        dynamicWebGetter?.cancel()
        currentCall?.cancel()
    }
}