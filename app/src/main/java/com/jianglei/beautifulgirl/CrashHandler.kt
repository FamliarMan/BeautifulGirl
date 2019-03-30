package com.jianglei.beautifulgirl

/**
 * @author jianglei on 3/30/19.
 */
class CrashHandler :Thread.UncaughtExceptionHandler {
    private lateinit var defaultHandler:Thread.UncaughtExceptionHandler
    override fun uncaughtException(p0: Thread?, p1: Throwable?) {
    }
}
