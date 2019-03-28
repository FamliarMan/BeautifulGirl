package com.jianglei.beautifulgirl

import androidx.multidex.MultiDexApplication
import com.elvishew.xlog.XLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.storage.DataStorage
import com.jianglei.ruleparser.LogUtil
import com.kk.taurus.exoplayer.ExoMediaPlayer
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.uuzuche.lib_zxing.activity.ZXingLibrary

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

    }
}