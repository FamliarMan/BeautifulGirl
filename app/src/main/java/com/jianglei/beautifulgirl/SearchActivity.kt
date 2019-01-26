package com.jianglei.beautifulgirl

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.SearchSource
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.SearchVideoKeyWord
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {

    private lateinit var adapter: CommonRecyclerAdapter<SearchVideoKeyWord>
    private var keyWords: MutableList<SearchVideoKeyWord> = ArrayList()
    private var webDataSource: WebDataSource? = null
    private var lastInputTime = 0L
    private var searchText: String? = null
    private var searchRunnable: Runnable = Runnable { search() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val dataSourceId = intent.getStringExtra("dataSourceId")
        webDataSource = WebSourceCenter.getWebSource(dataSourceId)
        if (webDataSource == null) {
            return
        }
        adapter = object : CommonRecyclerAdapter<SearchVideoKeyWord>(
            this, R.layout.listitem_xvideos_search,
            keyWords
        ) {
            override fun onUpdate(helper: BaseAdapterHelper?, item: SearchVideoKeyWord?, position: Int) {
                helper?.setText(R.id.tvKeyword, item?.N)
                helper?.setText(R.id.tvNumber, item?.R)
            }
        }

        rvSearchResult.layoutManager = LinearLayoutManager(this)
        rvSearchResult.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val word = adapter.getItem(position)
            goSearchResultActivity(
                word.N
            )
        }
        svSearch.onActionViewExpanded()
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) {
                    return false
                }
                goSearchResultActivity(query)
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText
                if (lastInputTime != 0L && System.currentTimeMillis() - lastInputTime < 1000) {
                    webDataSource!!.cancelAllNet()
                    rvSearchResult.removeCallbacks(searchRunnable)
                }
                rvSearchResult.postDelayed(searchRunnable, 500)
                lastInputTime = System.currentTimeMillis()
                return false
            }
        })


    }

    fun goSearchResultActivity(searchTxt: String) {

        val searchSource = webDataSource!! as SearchSource
        val category = Category("searchResult", searchSource.getSearchUrl(searchTxt),Category.TYPE_VIDEO)
        val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
        intent.putExtra("category", category)
        intent.putExtra("dataSourceId", webDataSource!!.id)
        startActivity(intent)
    }

    fun search() {
        if (searchText == null || searchText == "") {
            adapter.clear()
            return
        }
        val searchSource = webDataSource!! as SearchSource
        searchSource.getSearchSuggest(searchText!!, object : OnDataResultListener<MutableList<SearchVideoKeyWord>> {
            override fun onSuccess(data: MutableList<SearchVideoKeyWord>) {
                adapter.clear()
                adapter.addAll(data)
            }

            override fun onError(msg: String) {

            }
        })
    }

}
