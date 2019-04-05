package com.jianglei.girlshow.storage

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 *@author longyi created on 19-3-28
 */
@Dao
interface RuleDao {
    @Query("SELECT * FROM RuleRecord")
    fun getAllRules(): LiveData<List<RuleRecord>>

    @Update
    fun updateRule(rule:RuleRecord)

    @Insert
    fun addRule(rule:RuleRecord)

    @Delete
    fun deleteRule(rule:RuleRecord)
}