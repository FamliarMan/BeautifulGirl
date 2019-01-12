package com.jianglei.beautifulgirl

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        setSupportActionBar(toolBar)
        toolBar.setTitleTextColor(ContextCompat.getColor(this,android.R.color.white))
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val view = LayoutInflater.from(this).inflate(layoutResID, null, false)
        main_layout.addView(view, params)
        progress.setOnTouchListener { _, _ -> true }
    }

    fun showProgress(isShow: Boolean) {
        if (isShow) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }
}
