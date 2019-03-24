package com.jianglei.videoplay

import android.os.Parcel
import android.os.Parcelable

/**
 * @author jianglei on 3/24/19.
 */
data class ContentVo(
    var name: String?,
    var url: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentVo> {
        override fun createFromParcel(parcel: Parcel): ContentVo {
            return ContentVo(parcel)
        }

        override fun newArray(size: Int): Array<ContentVo?> {
            return arrayOfNulls(size)
        }
    }
}
