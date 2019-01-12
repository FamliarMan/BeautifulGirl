package com.jianglei.beautifulgirl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.WebsiteCenter
import com.jianglei.beautifulgirl.video.VideoPlayActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataSourceCenter.init()
        test.setOnClickListener {
//            val intent = Intent(this@MainActivity,AllWebsiteActivity::class.java)
//            intent.putParcelableArrayListExtra("websites", WebsiteCenter.getAllNormalWebsites())

            val intent = Intent(this@MainActivity,SearchActivity::class.java)
            intent.putExtra("dataSourceKey",DataSourceCenter.SOURCE_XVIDEOS_VIDE)
            startActivity(intent)
        }

        vpn.setOnClickListener {
            val intent = Intent(this@MainActivity,AllWebsiteActivity::class.java)
            intent.putParcelableArrayListExtra("websites", WebsiteCenter.getAllVpnWebsites())
            startActivity(intent)
        }

    }
}
