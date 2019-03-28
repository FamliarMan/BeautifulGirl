package com.jianglei.beautifulgirl.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 *@author longyi created on 19-3-28
 */
@Dao
interface RuleDao {
    @Query("SELECT * FROM RuleRecord")
    fun getAllRules(): LiveData<List<RuleRecord>>
}