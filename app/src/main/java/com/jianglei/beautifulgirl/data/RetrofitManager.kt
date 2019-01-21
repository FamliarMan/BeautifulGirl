package com.jianglei.beautifulgirl.data

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utils.IpUtils
import java.io.File
import java.nio.charset.Charset

/**
 * @author jianglei on 1/4/19.
 */

class RetrofitManager {
    companion object {
        lateinit var retrofit: Retrofit
        private var currentCall: Call<ResponseBody>? = null
        fun init(context: Context) {
            val logInterceptor =HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okhttpClient = OkHttpClient.Builder()
                .cache(Cache(File(getCacheDir(context) + "/beautiful"), 1024 * 1024 * 5))
                .addInterceptor(logInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-Forwarded-For",IpUtils.getRandomIp())
                        .addHeader("Accept-Language"," zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .build()
                    chain.proceed(request)
                }
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl("http://127.0.0.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpClient)
                .build()
        }

        private fun getCacheDir(context: Context): String {
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !Environment.isExternalStorageRemovable()
            ) {
                if(context.externalCacheDir == null){
                    context.cacheDir.path
                }else{
                    context.externalCacheDir!!.path
                }
            } else {
                context.cacheDir.path
            }
        }

        fun getWebsiteHtml(url: String, listener: OnWebResultListener, charset: String = "utf-8") {
            currentCall = retrofit.create(WebService::class.java)
                .fetchHtmlFromWebsite(url)
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

        fun get91Html(url: String, listener: OnWebResultListener, charset: String = "utf-8") {
            currentCall = retrofit.create(WebService::class.java)
                .fetchHtmlFromWebsite(url,IpUtils.getRandomIp())
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
        fun cancelNet() {
            if (currentCall != null) {
                currentCall!!.cancel()
                Log.d("jianglei","cancel net:"+ currentCall!!.request().url().toString())
            }
        }
    }


}
