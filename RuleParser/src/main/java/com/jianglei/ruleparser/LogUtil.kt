package com.jianglei.ruleparser

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import java.io.File

/**
 * @author jianglei on 3/24/19.
 */
class LogUtil {
    companion object {
        private var open: Boolean = false
        private var logHtml: Boolean = false
        fun isHtmlLogOpen():Boolean{
            return logHtml
        }
        fun openHtmlLog(isOpen: Boolean) {
            logHtml = isOpen
        }

        fun init(logOpen: Boolean, isFile: Boolean) {
            LogUtil.open = logOpen
            LogUtil.logHtml = logHtml
            if (!isFile) {
                XLog.init()
                return
            }
            val dir = Environment.getExternalStorageDirectory().toString() + "/BeautifulGirl/log"
            val file = File(dir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val filePrinter = FilePrinter.Builder(
                dir
            )
                .fileNameGenerator(ChangelessFileNameGenerator("log"))
                .cleanStrategy(FileLastModifiedCleanStrategy(60 * 60 * 100))
                .build()
            XLog.init(AndroidPrinter(), filePrinter)
        }

        fun d(msg: String) {
            XLog.d(msg)
        }

        fun openLog(context: Context) {

            val dir = Environment.getExternalStorageDirectory().toString() + "/BeautifulGirl/log/log"
            val file = File(dir)
            if (!file.exists()) {
                Toast.makeText(context, "没有找到日志文件,您可能为授予磁盘读写权限", Toast.LENGTH_LONG).show()
                return
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = getAvaliableUri(context,dir)
            intent.setDataAndType(uri, "text/plain")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent)


        }
    }

}

