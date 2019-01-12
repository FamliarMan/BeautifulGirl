package com.jianglei.beautifulgirl

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.WebsiteVo
import com.jianglei.beautifulgirl.R
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_category.*
import utils.DensityUtils
import utils.ToastUtils

class CategoryActivity : BaseActivity() {

    private var categories: MutableList<Category> = ArrayList()
    private var dataSourceKey: String? = null
    private var dataSource: DataSource? = null
    private var websiteVo: WebsiteVo? = null
    private var page = 1
    private lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        initToolbar()
        dataSourceKey = intent.getStringExtra("dataSourceKey")
        websiteVo = intent.getParcelableExtra("websiteVo")
        if (dataSourceKey == null || websiteVo == null) {
            return
        }
        dataSource = DataSourceCenter.getDataSource(dataSourceKey!!)
        rvCategory.setGridLayout(2)
        adapter = CategoryAdapter(this, categories)
        rvCategory.setAdapter(adapter)
        adapter.onItemClickListener = (object : OnItemClickListener<Category> {
            override fun onItemClick(vo: Category, pos: Int) {
                val intent = Intent(this@CategoryActivity, ContentActivity::class.java)
                val types = java.util.ArrayList<Category>()
                types.add(vo)
                intent.putParcelableArrayListExtra("types", types)
                intent.putExtra("dataSourceKey", dataSourceKey)
                startActivity(intent)
            }
        })


        rvCategory.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                fetchData()

            }

            override fun onRefresh() {
                page = 1
                fetchData()

            }
        })
        rvCategory.setRefreshing(true)
        fetchData()

    }

    private fun initToolbar() {
        toolBar.setOnMenuItemClickListener(object : android.support.v7.widget.Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when {
                    item?.itemId == R.id.action_search-> {
                        val intent = Intent(this@CategoryActivity,SearchActivity::class.java)
                        intent.putExtra("dataSourceKey",dataSourceKey)
                        startActivity(intent)

                    }
                    else -> {
                        return true
                    }
                }
                return true
            }
        })
    }


    private fun fetchData() {
        dataSource!!.fetAllTypes(websiteVo!!.homePageUrl, object : OnDataResultListener<MutableList<Category>> {
            override fun onSuccess(data: MutableList<Category>) {
                page++
                if (data.size == 0) {
                    ToastUtils.showMsg(
                        this@CategoryActivity,
                        getString(R.string.no_more_data)
                    )
                    rvCategory.pushRefreshEnable = false
                    return
                }
                data.forEach {
                    Log.d("longyi", it.title + " " + it.coverUrl)
                }
                categories.addAll(data)
                adapter.notifyItemInserted(categories.size - data.size)
                rvCategory.post {
                    rvCategory.setPullLoadMoreCompleted()
                }

            }

            override fun onError(msg: String) {
                rvCategory.post {
                    rvCategory.setPullLoadMoreCompleted()
                }
                ToastUtils.showMsg(this@CategoryActivity, msg)
            }

        }, page)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
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
            holder.ivCover.setImageURI(Uri.parse(category.coverUrl))
            holder.ivCover.setOnClickListener {
                onItemClickListener?.onItemClick(category, position)
            }
        }

    }

    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}
