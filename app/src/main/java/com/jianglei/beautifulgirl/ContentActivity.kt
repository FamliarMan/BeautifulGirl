package com.jianglei.beautifulgirl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        val fragments = ArrayList<Fragment>()
        fragments.add(PictureListFragment())
        fragments.add(PictureListFragment())
        val titles = ArrayList<String>()
        titles.add("美女")
        titles.add("美女")
        val adapter = ContentFragmentAdapter(supportFragmentManager, fragments,titles)
        vpContent.adapter = adapter
        tab.setupWithViewPager(vpContent)
    }
}
