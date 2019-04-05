package com.jianglei.girlshow

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import android.widget.Toast
import com.jianglei.ruleparser.LogUtil
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import com.facebook.common.logging.FLogDefaultLoggingDelegate.sInstance


/**
 * @author jianglei on 3/30/19.
 */
class CrashHandler : Thread.UncaughtExceptionHandler {
    private lateinit var defaultHandler: Thread.UncaughtExceptionHandler
    private lateinit var mContext: Context
    // 保存手机信息和异常信息
    private val mMessage = HashMap<String, String>()

    companion object {
        private var instance: CrashHandler? = null
        fun getInstance(): CrashHandler {
            if (instance == null) {
                synchronized(CrashHandler::class.java) {
                    if (instance == null) {
                        synchronized(CrashHandler::class.java) {
                            instance = CrashHandler()
                        }
                    }
                }
            }
            return instance!!
        }

    }

    fun init(context: Context) {
        mContext = context
        // 获取默认异常处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtil.d(e.toString())
        if (!handleException(e)) {
            // 未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            defaultHandler.uncaughtException(t, e)
        } else {
            // 已经人为处理,系统自己退出
            try {
                Thread.sleep(1000)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
            android.os.Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private fun handleException(e: Throwable?): Boolean {
        if (e == null) {// 异常是否为空
            return false
        }
//        collectErrorMessages()
        saveErrorMessages(e)
        return false
    }

    /**
     * 1.收集错误信息
     */
    private fun collectErrorMessages() {
        val pm = mContext.packageManager
        try {
            val pi = pm.getPackageInfo(mContext.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (TextUtils.isEmpty(pi.versionName)) "null" else pi.versionName
                val versionCode = "" + pi.versionCode
                mMessage.put("versionName", versionName)
                mMessage.put("versionCode", versionCode)
            }
            // 通过反射拿到错误信息
            val fields = Build::class.java.fields
            if (fields != null && fields.size > 0) {
                for (field in fields) {
                    field.isAccessible = true
                    try {
                        mMessage.put(field.name, field.get(null).toString())
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    /**
     * 2.保存错误信息
     *
     * @param e Throwable
     */
    private fun saveErrorMessages(e: Throwable) {
        val sb = StringBuilder()
        mMessage.forEach {
            val key = it.key
            val value = it.value
            sb.append(key).append("=").append(value).append("\n")
        }
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        e.printStackTrace(pw)
        var cause: Throwable? = e.cause
        // 循环取出Cause
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = e.cause
        }
        pw.close()
        val result = writer.toString()
        sb.append(result)
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
        val fileName = "crash-" + time + "-" + System.currentTimeMillis() + ".log"
        // 有无SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            val path = LogUtil.getDirectory()
            val dir = File(path)
            if (!dir.exists()) dir.mkdirs()
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(path + fileName)
                fos!!.write(sb.toString().toByteArray())
            } catch (e1: Exception) {
                e1.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
            }
        }
    }
}
