package com.jianglei.beautifulgirl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.spider.PictureTitleVo
import com.jianglei.beautifulgirl.spider.FanliSpider
import com.jianglei.beautifulgirl.spider.SpiderResultListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataSourceCenter.init()
        test.setOnClickListener {
            val intent = Intent(this@MainActivity, ContentActivity::class.java)
            startActivity(intent)
        }
    }
}
