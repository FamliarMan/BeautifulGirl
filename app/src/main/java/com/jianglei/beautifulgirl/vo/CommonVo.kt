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

data class PictureTypeVo(var title: String, var url: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(url)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PictureTypeVo> = object : Parcelable.Creator<PictureTypeVo> {
            override fun createFromParcel(source: Parcel): PictureTypeVo = PictureTypeVo(source)
            override fun newArray(size: Int): Array<PictureTypeVo?> = arrayOfNulls(size)
        }
    }
}

