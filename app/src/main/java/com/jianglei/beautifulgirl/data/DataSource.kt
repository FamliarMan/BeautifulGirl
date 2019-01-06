package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.PictureTitleVo

/**
 * 一个具体的数据来源，比如爬虫，比如接口
 * @author jianglei on 1/3/19.
 */
interface DataSource {
    /**
     * 获取第几页的数据，[page]为页码，从1开始，需要传入监听[OnDataResultListener],
     * 以便获取数据
     */
     fun  fetchTitles(page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>)

    /**
     * 获取某个帖子的所有图片数据
     */
    fun fetDetailPictures(url:String,page:Int,listener: OnDataResultListener<MutableList<String>>)


}