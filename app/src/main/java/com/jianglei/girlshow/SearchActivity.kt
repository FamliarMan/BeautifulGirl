package com.jianglei.girlshow

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.ruleparser.data.OnDataResultListener
import com.jianglei.girlshow.vo.SearchVideoKeyWord
import com.jianglei.ruleparser.RuleKeyWord
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {

    private lateinit var adapter: CommonRecyclerAdapter<SearchVideoKeyWord>
    private var keyWords: MutableList<SearchVideoKeyWord> = ArrayList()
    private var lastInputTime = 0L
    private var searchText: String? = null
    private var searchRunnable: Runnable = Runnable { search() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
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
                    StrategyProvider.getCurStrategy()!!.cancel()
                    rvSearchResult.removeCallbacks(searchRunnable)
                }
                rvSearchResult.postDelayed(searchRunnable, 500)
                lastInputTime = System.currentTimeMillis()
                return false
            }
        })


    }

    fun goSearchResultActivity(searchTxt: String) {

        val searchRule = StrategyProvider.getCurStrategy()!!
            .webRule.searchRule
        val url = searchRule!!.searchUrl.replace(RuleKeyWord.SEARCH_TXT, searchTxt)
        SearchResultActivity.start(this, url)
    }

    fun search() {
        if (searchText == null || searchText == "") {
            adapter.clear()
            return
        }
        StrategyProvider.getCurStrategy()!!
            .fetchSearchSuggest(searchText!!,
                object : OnDataResultListener<List<SearchVideoKeyWord>> {
                    override fun onSuccess(data: List<SearchVideoKeyWord>) {
                        adapter.clear()
                        adapter.addAll(data)
                    }

                    override fun onError(msg: String) {
                    }
                })
    }

}
