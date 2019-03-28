package com.jianglei.beautifulgirl.storage

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

)
