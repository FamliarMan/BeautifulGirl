package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.FanliSpider
import com.jianglei.beautifulgirl.spider.ZhuangxiuSpider
import com.jianglei.beautifulgirl.spider.vpn.CaoliuSpider
import com.jianglei.beautifulgirl.spider.vpn.NineOnePornSpider

/**
 * @author jianglei on 1/3/19.
 */

object DataSourceCenter {
    private val allDataSources = HashMap<String, DataSource>()
    /**
     * 注册一个数据源
     */
    fun registerDataSource(key: String, dataSource: DataSource) {
        allDataSources[key] = dataSource
    }

    /**
     * 获取一个数据源
     */
    fun getDataSource(key: String): DataSource? {
        return allDataSources[key]
    }

    fun init() {
        registerDataSource(SOURCE_FANLI_PICTURE, FanliSpider())
        registerDataSource(SOURCE_ZHUANGXIU_PICTURE, ZhuangxiuSpider())
        registerDataSource(SOURCE_CAOLIU_PICTURE, CaoliuSpider())
        registerDataSource(SOURCE_91_VIDEO,NineOnePornSpider())
    }

    /**
     * 饭粒动态图的图片
     */
    const val SOURCE_FANLI_PICTURE = "fanli"
    /**
     * 妆秀的图片
     */
    const val SOURCE_ZHUANGXIU_PICTURE = "zhaungxiu"

    /**
     * 草榴的图片
     */
    const val SOURCE_CAOLIU_PICTURE = "caoliu"

    /**
     * 91的视频
     */
    const val SOURCE_91_VIDEO = "91video"

}

