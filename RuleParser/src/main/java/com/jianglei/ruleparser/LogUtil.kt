package com.jianglei.ruleparser

import android.app.Application
import android.os.Environment
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import java.io.File

/**
 * @author jianglei on 3/24/19.
 */
class LogUtil {
    companion object {
        private var open: Boolean = false
        var logHtml: Boolean = false
        fun initOnlyConsole(logOpen: Boolean, logHtml: Boolean){
            init(logOpen,logHtml,false)

        }
        fun init(logOpen: Boolean, logHtml: Boolean,isFile:Boolean) {
            LogUtil.open = logOpen
            LogUtil.logHtml = logHtml
            if(!isFile){
                XLog.init()
                return
            }
            val dir = Environment.getExternalStorageDirectory().toString() + "/BeautifulGirl"
            val file = File(dir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val filePrinter = FilePrinter.Builder(
                dir
            )
                .fileNameGenerator(DateFileNameGenerator())
                .cleanStrategy(FileLastModifiedCleanStrategy(24 * 60 * 60 * 100))
                .build()
            XLog.init(AndroidPrinter(), filePrinter)
        }

        fun d(msg: String) {
            XLog.d(msg)
        }
    }
}