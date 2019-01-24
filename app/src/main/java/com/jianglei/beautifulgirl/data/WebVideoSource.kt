package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.vo.PlayUrl

/**
 * 如果网站数据是视频的，实现这个接口
 * @author jianglei on 1/23/19.
 */
interface WebVideoSource :WebDataSource{
    /**
     * 获取视频地址
     */
    fun fetchVideoUrls(detailUrl: String, listener: OnDataResultListener<MutableList<PlayUrl>>)

}