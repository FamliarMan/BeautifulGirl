package utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.jianglei.beautifulgirl.R

/**
 *@author longyi created on 19-3-25
 */
class DialogUtils {
    companion object {
        /**
         * 显示提示对话框
         */
        fun showTipDialog(context: Context?, msg: String?) {
            if (context == null || (context is Activity && context.isFinishing)) {
                return
            }
            val builder = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_tip))
                .setMessage(msg)
                .setCancelable(true)
            builder.create().show()
        }
    }
}