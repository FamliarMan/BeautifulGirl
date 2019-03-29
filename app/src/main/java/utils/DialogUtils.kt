package utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.jianglei.beautifulgirl.R
import com.jianglei.ruleparser.LogUtil

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

        fun showClickDialog(
            context: Context?,
            msg: String?,
            rightText: String,
            listener: DialogInterface.OnClickListener?
        ) {
            if (context == null || (context is Activity && context.isFinishing)) {
                return
            }

            val builder = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_tip))
                .setMessage(msg)
                .setCancelable(true)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .setPositiveButton(rightText, listener)
            builder.create().show()
        }


        fun showLogTipDialog(context: Context, msg: String) {
            DialogUtils.showClickDialog(context, msg, context.getString(R.string.check_detail_log),
                DialogInterface.OnClickListener { _, _ ->
                    LogUtil.openLog(context)
                })
        }
    }
}