package com.jianglei.beautifulgirl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.vo.Category
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_category.*
import utils.DialogUtils
import utils.ToastUtils

class CategoryActivity : BaseActivity() {

    private var categories: MutableList<Category> = ArrayList()
    private var page = 1
    private lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        initToolbar()
        rvCategory.setGridLayout(2)
        adapter = CategoryAdapter(this, categories)
        rvCategory.setAdapter(adapter)
        if(!StrategyProvider.getCurStrategy()!!.webRule.categoryRule!!.supportPage){
            rvCategory.pushRefreshEnable = false
        }
        adapter.onItemClickListener = (object : OnItemClickListener<Category> {
            override fun onItemClick(vo: Category, pos: Int) {
                val intent = Intent(this@CategoryActivity, ContentActivity::class.java)
                val types = java.util.ArrayList<Category>()
                types.add(vo)
                intent.putParcelableArrayListExtra("types", types)
//                intent.putExtra("dataSourceId", webDataSource!!.id)
                startActivity(intent)
            }
        })


        rvCategory.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                try {
                    fetchData()
                } catch (e: Throwable) {
                    DialogUtils.showLogTipDialog(this@CategoryActivity, e.toString())
                }

            }

            override fun onRefresh() {
                page = 1
                try {
                    fetchData()
                } catch (e: Throwable) {
                    DialogUtils.showLogTipDialog(this@CategoryActivity, e.toString())
                }

            }
        })
        rvCategory.setRefreshing(true)
        try {
            fetchData()
        } catch (e: Throwable) {
            DialogUtils.showLogTipDialog(this@CategoryActivity, e.toString())
        }

    }

    private fun initToolbar() {
        toolBar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            when {
                item?.itemId == R.id.action_search -> {
                    val intent = Intent(this@CategoryActivity, SearchActivity::class.java)
                    startActivity(intent)

                }
                else -> {
                    return@OnMenuItemClickListener true
                }
            }
            true
        })
    }


    private fun fetchData() {
        StrategyProvider.getCurStrategy()!!
            .fetchAllCategory(
                this,
                page,
                object : OnDataResultListener<List<Category>> {
                    override fun onSuccess(data: List<Category>) {

                        page++
                        if (data.isEmpty()) {
                            DialogUtils.showLogTipDialog(
                                this@CategoryActivity,
                                getString(R.string.no_more_data)
                            )
                            rvCategory.pushRefreshEnable = false
                        } else {
                            categories.addAll(data)
                            adapter.notifyItemInserted(categories.size - data.size)
                        }
                        rvCategory.post {
                            rvCategory.setPullLoadMoreCompleted()
                        }

                    }

                    override fun onError(msg: String) {
                        rvCategory.post {
                            rvCategory.setPullLoadMoreCompleted()
                        }
                        DialogUtils.showLogTipDialog(this@CategoryActivity, msg)
                    }
                }
            )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (StrategyProvider.getCurStrategy()!!.webRule.supportSearch) {
            menuInflater.inflate(R.menu.search, menu)
            true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    class CategoryAdapter(var context: Context, var categories: MutableList<Category>) :
        RecyclerView.Adapter<CategoryHolder>() {
        var onItemClickListener: OnItemClickListener<Category>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
            val view = LayoutInflater.from(context).inflate(
                R.layout.listitem_category,
                parent, false
            )
            return CategoryHolder(view)
        }

        override fun getItemCount(): Int {
            return categories.size
        }

        override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
            val category = categories[position]
            holder.tvName.text = category.title
            if (category.coverUrl == null) {
                holder.ivCover.visibility = View.GONE
            } else {
                holder.ivCover.visibility = View.VISIBLE
                holder.ivCover.setImageURI(Uri.parse(category.coverUrl))
            }
            holder.layoutMain.setOnClickListener {
                onItemClickListener?.onItemClick(category, position)
            }
        }

    }

    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var layoutMain: CardView = itemView.findViewById(R.id.layout_main)
    }
}
