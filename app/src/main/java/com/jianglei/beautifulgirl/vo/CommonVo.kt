package com.jianglei.beautifulgirl.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * @author jianglei on 1/6/19.
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

/**
 * 一个网站基本的描述，[name] 是网站标题，
 * [homePageUrl]是开始抓取的页面url，
 * [icon]是网站的图标
 * [type] 网站的类型藐视，可以是“图片”或“视频”
 */
data class WebsiteDescVo(
    var name: String,
    var homePageUrl: String,
    var icon: Int,
    var type: String
)

/**
 * [title]分类标题，[url]分类的url，[type]分类的类型
 */
data class Category(var title: String, var url: String,var type:Int) : Parcelable {

    /**
     * 封面url
     */
    var coverUrl: String? = null

    /**
     * 分类的描述
     */
    var desc:String? = null

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readInt()
    ) {
        coverUrl = source.readString()
        desc = source.readString()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(url)
        writeInt(type)
        writeString(coverUrl)
        writeString(desc)
    }

    companion object {
        const val TYPE_PICTURE = 0

        const val TYPE_VIDEO = 1

        const val TYPE_BOOK = 2

        @JvmField
        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category> {
            override fun createFromParcel(source: Parcel): Category = Category(source)
            override fun newArray(size: Int): Array<Category?> = arrayOfNulls(size)
        }
    }
}



/**
 * xvideos搜索返回的关键字返回结构
 */
data class XVideoKeyWordWrapper(var KEYWORDS:MutableList<SearchVideoKeyWord>)
/**
 * 搜索返回的关键字
 */
data class SearchVideoKeyWord(var N:String, var R:String)

/**
 * [defaultQuality]: 是否是默认选中
 * [format]: 当前视频格式
 * [quality]: 当前视频质量
 * [videoUrl]: 当前视频地址
 */
data class PlayUrl(var defaultQuality:Boolean,var format:String,var quality:String,var videoUrl:String )
