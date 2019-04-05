package com.jianglei.girlshow.storage

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *@author longyi created on 19-3-28
 */

@Entity(tableName = "RuleRecord")
data class RuleRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,
    /**
     * 该规则是否激活使用，0不使用，1使用
     */
    @ColumnInfo(name = "enabled")
    var enabled: Int,
    /**
     * 具体的存储规则的json串
     */
    @ColumnInfo(name = "rule")
    var rule: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(enabled)
        parcel.writeString(rule)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RuleRecord> {
        override fun createFromParcel(parcel: Parcel): RuleRecord {
            return RuleRecord(parcel)
        }

        override fun newArray(size: Int): Array<RuleRecord?> {
            return arrayOfNulls(size)
        }
    }
}
