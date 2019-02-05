package com.jianglei.beautifulgirl.vo

/**
 * @author jianglei on 1/2/19.
 */
data class ContentTitle(val title:String, val desc:String,
                        val detailUrl:String, val coverUrl:String,
                        val type:Int){

    //是否采用webview播放，有些视频网站有加密，获取真实url难度太大，只能直接跳转到web页面
    var isUseWeb:Boolean = false
}
