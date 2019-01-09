package com.jianglei.beautifulgirl.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * @author jianglei on 1/6/19.
 */
data class WebsiteVo(var name: String, var homePageUrl: String, var icon: Int, var dataSourceKey: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(homePageUrl)
        writeInt(icon)
        writeString(dataSourceKey)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WebsiteVo> = object : Parcelable.Creator<WebsiteVo> {
            override fun createFromParcel(source: Parcel): WebsiteVo = WebsiteVo(source)
            override fun newArray(size: Int): Array<WebsiteVo?> = arrayOfNulls(size)
        }
    }
}

data class Category(var title: String, var url: String) : Parcelable {
    /**
     *当前分类下内容的类型
     */
    var type: Int = TYPE_PICTURE

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    ) {
        type = source.readInt()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(url)
        writeInt(type)
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

