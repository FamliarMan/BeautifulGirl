package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.PictureTitleVo

/**
 * @author jianglei on 1/3/19.
 */
interface DataProvider {
    /**
     * 获取第几页的数据，[page]为页码，从1开始，需要传入监听[OnDataResultListener],
     * 以便获取数据
     */
    fun  fetchData(url:String ,page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>)

    /**
     * 获取某个详情页面的所有内容
     */
    fun  fetchDetailList( url:String ,page:Int,listener: OnDataResultListener<MutableList<String>>)

    /**
     * 是否可以切换数据来源，比如从爬虫切换从接口获取
     */
    fun canChangeDataSource(): Boolean

    /**
     * 切换数据来源,[dataSource]为新的数据来源
     */
    fun changeDataSource(dataSource: DataSource)
}
