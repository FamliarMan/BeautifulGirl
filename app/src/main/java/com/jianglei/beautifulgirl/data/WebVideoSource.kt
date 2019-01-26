package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.vo.PlayContent
import com.jianglei.beautifulgirl.vo.PlayUrl

/**
 * 如果网站数据是视频的，实现这个接口
 * @author jianglei on 1/23/19.
 */
interface WebVideoSource :WebDataSource{
    /**
     * 获取视频地址
     * 有时候一个图片封面进来可能会对应多个视频，所以这里是一个列表
     */
    fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayContent>>)

}