package com.jianglei.beautifulgirl

import android.support.v4.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD

/**
 * @author jianglei on 1/23/19.
 */
open class BaseFragment : Fragment() {

    private lateinit var progress: KProgressHUD
    fun showProgress(isShow: Boolean) {

        if (isShow) {
            if (activity == null) {
                return
            }
            progress = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        } else {
            progress.dismiss()
        }
    }
}