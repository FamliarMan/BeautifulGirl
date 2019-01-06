package utils

import android.content.Context
import android.widget.Toast

/**
 * @author jianglei on 1/6/19.
 */
object ToastUtils {
    fun showMsg(context: Context,msg:String ){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }
}