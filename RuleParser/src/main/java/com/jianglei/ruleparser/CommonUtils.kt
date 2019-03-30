package com.jianglei.ruleparser

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author jianglei on 3/30/19.
 */

fun getAvaliableUri(context: Context, filePath: String): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            context, "com.jianglei.beautifulgirl.fileprovider",
            File(filePath)
        )
    } else {
        Uri.fromFile(File(filePath))
    }
}
