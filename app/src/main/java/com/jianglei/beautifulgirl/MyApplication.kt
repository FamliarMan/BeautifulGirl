package com.jianglei.beautifulgirl

import android.app.Application
import com.jianglei.beautifulgirl.data.RetrofitManager

/**
 * @author jianglei on 1/4/19.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitManager.init(this)
    }
}