package com.jianglei.beautifulgirl.storage

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.rule.RuleCenter
import java.util.concurrent.Executors

/**
 *@author longyi created on 19-3-28
 */
class DataStorage {
    companion object {
        lateinit var db: AppDataBase
        val excutorService = Executors.newCachedThreadPool()
        val mainHandler = Handler(Looper.getMainLooper())


        fun init(context: Context) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "beautiful_girl"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val caoliu = RuleCenter.caoliu
                    val insert = "INSERT INTO RuleRecord (name,enabled,rule) values(?,1,?)"
                    db.execSQL(insert, arrayOf("草榴",caoliu))
                    db.execSQL(insert, arrayOf("xvideos",RuleCenter.xvideos))
                    db.execSQL(insert, arrayOf("饭粒",RuleCenter.fanli))
                    db.execSQL(insert, arrayOf("pornhub",RuleCenter.pornhub))
                    db.execSQL(insert, arrayOf("91porn",RuleCenter.nineone))
                }
            }).build()
        }

        fun <E> sqlOperation(listener: OnSqlExcuteListener<E>) {
            excutorService.execute {
                val res = listener.onChildThread()
                mainHandler.post {
                    listener.onMainThread(res)
                }
            }

        }
    }
}

