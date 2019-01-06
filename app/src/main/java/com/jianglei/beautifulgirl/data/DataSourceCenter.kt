package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.FanliSpider

/**
 * @author jianglei on 1/3/19.
 */

object DataSourceCenter {
    private val allDataSources = HashMap<String, DataSource>()
    /**
     * 注册一个数据源
     */
    fun registerDataSource(key: String, dataSource: DataSource) {
        allDataSources.put(key, dataSource)
    }

    /**
     * 获取一个数据源
     */
    fun getDataSource(key: String): DataSource? {
        return allDataSources[key]
    }

    fun init(){
        registerDataSource(SOURCE_FANLI_PICTURE,FanliSpider() as DataSource)
    }
    /**
     * 饭粒动态图的图片
     */
    const val SOURCE_FANLI_PICTURE = "fanli"

}

