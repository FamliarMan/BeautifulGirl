package com.jianglei.beautifulgirl.vo

/**
 * @author jianglei on 1/2/19.
 */
data class ContentTitle(val title:String, val desc:String, val detailUrl:String, val coverUrl:String){
    var type:Int = Category.TYPE_PICTURE
}
