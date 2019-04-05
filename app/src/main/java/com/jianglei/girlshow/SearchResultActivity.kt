package com.jianglei.girlshow

import android.content.Context
import android.content.Intent
import android.os.Bundle

class SearchResultActivity : BaseActivity() {

    companion object {

        fun start(context: Context, url: String) {
            val intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        val url = intent.getStringExtra("url")
        if (savedInstanceState == null) {
            val fragment = ContentCoverListFragment.newInstance(
                "搜索结果",
                url,
                true
            )
            supportFragmentManager.beginTransaction().add(R.id.container, fragment)
                .commit()
        }
    }
}
