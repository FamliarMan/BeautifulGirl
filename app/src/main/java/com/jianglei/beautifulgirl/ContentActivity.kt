package com.jianglei.beautifulgirl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import com.jianglei.beautifulgirl.data.SearchSource
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.rule.WebStrategy
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : BaseActivity() {
    private var pictureTypes: ArrayList<Category>? = null
    private var dataSource: WebDataSource? = null
    private lateinit var webStrategy: WebStrategy

    companion object {
        fun start(context: Context, category: List<Category>) {
            val intent = Intent(context,ContentActivity::class.java)
            intent.putParcelableArrayListExtra("types", category as java.util.ArrayList<out Parcelable>)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
//        val dataSourceId = intent.getStringExtra("dataSourceId")
//        dataSource = WebSourceCenter.getWebSource(dataSourceId)
        pictureTypes = intent.getParcelableArrayListExtra<Category>("types")
        webStrategy = StrategyProvider.getCurStrategy()!!
        if (pictureTypes == null) {
            Toast.makeText(this, "Wrong website url", Toast.LENGTH_LONG).show()
            return
        }
        initToolbar()
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        if (pictureTypes!!.size <= 4) {
            tab.tabMode = TabLayout.MODE_FIXED
        } else {
            tab.tabMode = TabLayout.MODE_SCROLLABLE
        }
        pictureTypes!!.forEach {
            fragments.add(ContentCoverListFragment.newInstance(it.title, it.url ))
            titles.add(it.title)
        }
        val adapter = ContentFragmentAdapter(supportFragmentManager, fragments, titles)
        vpContent.adapter = adapter
        tab.setupWithViewPager(vpContent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (dataSource is SearchSource) {
            menuInflater.inflate(R.menu.search, menu)
            true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    private fun initToolbar() {
        toolBar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            when {
                item?.itemId == R.id.action_search -> {
                    val intent = Intent(this@ContentActivity, SearchActivity::class.java)
                    intent.putExtra("dataSourceId", dataSource!!.id)
                    startActivity(intent)

                }
                else -> {
                    return@OnMenuItemClickListener true
                }
            }
            true
        })
    }
}
