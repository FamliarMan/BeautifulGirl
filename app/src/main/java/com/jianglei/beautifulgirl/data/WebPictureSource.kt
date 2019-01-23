package com.jianglei.beautifulgirl.data

/**
 * @author jianglei on 1/23/19.
 */
interface WebPictureSource : WebDataSource {
    /**
     * 获取某个帖子的所有图片数据
     */
    fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {}
}