package com.jianglei.beautifulgirl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.widget.Toast
import com.jianglei.beautifulgirl.vo.Category
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {
    private var pictureTypes: ArrayList<Category>? = null
    private var dataSourceKey:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        dataSourceKey = intent.getStringExtra("dataSourceKey")
        pictureTypes = intent.getParcelableArrayListExtra<Category>("types")
        if (pictureTypes == null || dataSourceKey == null) {
            Toast.makeText(this, "Wrong website url", Toast.LENGTH_LONG).show()
            return
        }
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        if(pictureTypes!!.size<=4){
            tab.tabMode = TabLayout.MODE_FIXED
        }else{
            tab.tabMode = TabLayout.MODE_SCROLLABLE
        }
        pictureTypes!!.forEach {
            fragments.add(PictureListFragment.newInstance(it.title, it.url,dataSourceKey!!))
            titles.add(it.title)
        }
        val adapter = ContentFragmentAdapter(supportFragmentManager, fragments, titles)
        vpContent.adapter = adapter
        tab.setupWithViewPager(vpContent)
    }
}
