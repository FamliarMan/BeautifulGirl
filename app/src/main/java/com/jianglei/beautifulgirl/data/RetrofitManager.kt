package com.jianglei.beautifulgirl.data

import android.content.Context
import android.os.Environment
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * @author jianglei on 1/4/19.
 */

class RetrofitManager {
    companion object {
        lateinit var retrofit: Retrofit

        fun init(context: Context) {
            val okhttpClient = OkHttpClient.Builder()
                .cache(Cache(File(getCacheDir(context) + "/beautiful"), 1024 * 1024 * 5))
                .addInterceptor(LogInterceptor())
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
                context.externalCacheDir.path
            } else {
                context.cacheDir.path
            }
        }

        fun getWebsiteHtml(url: String, listener: OnWebResultListener) {
            retrofit.create(WebService::class.java)
                .fetchHtmlFromWebsite(url)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        listener.onError(-1, t.localizedMessage)

                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        when {
                            response.code() == 404 -> {
                                listener.onError(404, "No data")
                            }
                            response.isSuccessful -> {
                                listener.onSuccess(response.body()?.string())
                            }
                            else -> {
                                listener.onError(response.code(), "Network Error")
                            }

                        }
                        response.body()?.close()
                    }

                })
        }

    }


}
