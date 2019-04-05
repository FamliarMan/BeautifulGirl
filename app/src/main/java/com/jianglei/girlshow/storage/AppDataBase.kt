package com.jianglei.girlshow.storage

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 *@author longyi created on 19-3-28
 */
@Database(entities = arrayOf(RuleRecord::class), version = 1,exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun ruleDao(): RuleDao
}