package com.jianglei.girlshow.storage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 *@author longyi created on 19-3-28
 */
class DataStorage {
    companion object {
        lateinit var db: AppDataBase


        fun init(context: Context) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "beautiful_girl"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }
            }).build()
        }

    }
}

