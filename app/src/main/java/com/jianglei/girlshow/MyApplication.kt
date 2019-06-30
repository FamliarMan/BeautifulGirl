package com.jianglei.girlshow

import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.jianglei.ruleparser.data.RetrofitManager
import com.jianglei.girlshow.storage.DataStorage
import com.kk.taurus.exoplayer.ExoMediaPlayer
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.imagepipeline.listener.RequestListener
import com.jianglei.ruleparser.LogUtil


/**
 * @author jianglei on 1/4/19.
 */
class MyApplication : MultiDexApplication() {
    companion object {
        public var ignoreMobile = true
    }
    override fun onCreate() {
        super.onCreate()
        RetrofitManager.init(this)
        Fresco.initialize(this)
        PlayerConfig.setUseDefaultNetworkEventProducer(true)
        //初始化库
        PlayerLibrary.init(this)
        ExoMediaPlayer.init(this)
        DataStorage.init(this)
        ZXingLibrary.initDisplayOpinion(this)
        CrashHandler.getInstance().init(this)
        LogUtil.openHtmlLog(true)

        val requestListeners = mutableSetOf<RequestLoggingListener>()
        requestListeners.add(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(this)
            // other setters
            .setRequestListeners(requestListeners as Set<RequestListener>?)
            .build()
//        Fresco.initialize(this, config)
//        FLog.setMinimumLoggingLevel(FLog.VERBOSE)

    }
}