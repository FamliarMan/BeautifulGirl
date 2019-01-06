package com.jianglei.beautifulgirl

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val view = LayoutInflater.from(this).inflate(layoutResID, null, false)
        main_layout.addView(view,params)
    }

    fun showProgress(isShow: Boolean) {
        if (isShow) {
            progress.visibility = View.VISIBLE
            main_layout.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
            main_layout.visibility = View.VISIBLE
        }
    }
}
