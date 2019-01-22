package com.jianglei.beautifulgirl

import android.os.Bundle
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.vo.Category

class SearchResultActivity : BaseActivity() {

    private var category: Category? = null
    private var dataSource: WebDataSource? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        dataSource = intent.getSerializableExtra("dataSource") as WebDataSource
        category = intent.getParcelableExtra("category")
        if (dataSource== null || category == null) {
            return
        }
        if (savedInstanceState == null) {
            val fragment = PictureListFragment.newInstance(
                category!!.title,
                category!!.url,
                dataSource!!,
                true
            )
            supportFragmentManager.beginTransaction().add(R.id.container, fragment)
                .commit()
        }
    }
}
