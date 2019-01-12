package com.jianglei.beautifulgirl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jianglei.beautifulgirl.vo.Category

class SearchResultActivity : BaseActivity() {

    private var category: Category? = null
    private var dataSourceKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        dataSourceKey = intent.getStringExtra("dataSourceKey")
        category = intent.getParcelableExtra("category")
        if (dataSourceKey == null || category == null) {
            return
        }
        if (savedInstanceState == null) {
            val fragment = PictureListFragment.newInstance(
                category!!.title,
                category!!.url,
                dataSourceKey!!,
                true
            )
            supportFragmentManager.beginTransaction().add(R.id.container, fragment)
                .commit()
        }
    }
}
