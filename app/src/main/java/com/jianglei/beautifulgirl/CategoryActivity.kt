package com.jianglei.beautifulgirl

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.WebsiteVo
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_category.*
import utils.ToastUtils

class CategoryActivity : AppCompatActivity() {

    private var categories: MutableList<Category> = ArrayList()
    private var dataSourceKey: String? = null
    private var dataSource: DataSource? = null
    private var websiteVo: WebsiteVo? = null
    private var page = 1
    private lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        dataSourceKey = intent.getStringExtra("dataSourceKey")
        websiteVo = intent.getParcelableExtra("websiteVo")
        if (dataSourceKey == null || websiteVo == null) {
            return
        }
        dataSource = DataSourceCenter.getDataSource(dataSourceKey!!)
        rvCategory.setGridLayout(2)
        adapter = CategoryAdapter(this, categories)
        rvCategory.setAdapter(adapter)
        rvCategory.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                fetchData()

            }

            override fun onRefresh() {
                page = 1
                fetchData()

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
                data.forEach{
                    Log.d("longyi",it.title+" "+it.coverUrl)
                }
                categories.addAll(data)
                adapter.notifyItemInserted(categories.size - data.size)
                rvCategory.setPullLoadMoreCompleted()

            }

            override fun onError(msg: String) {
                rvCategory.setPullLoadMoreCompleted()
                ToastUtils.showMsg(this@CategoryActivity, msg)
            }

        }, page)

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
