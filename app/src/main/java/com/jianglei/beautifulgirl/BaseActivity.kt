package com.jianglei.beautifulgirl

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {
    private var progress:KProgressHUD?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        setSupportActionBar(toolBar)
        toolBar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val view = LayoutInflater.from(this).inflate(layoutResID, null, false)
        main_layout.addView(view, params)
    }

    fun showProgress(isShow: Boolean) {

        if (isShow) {
            progress = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        } else {
            progress?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (StrategyProvider.getCurStrategy()!=null){
            StrategyProvider.getCurStrategy()!!.cancel()
        }
    }
}
