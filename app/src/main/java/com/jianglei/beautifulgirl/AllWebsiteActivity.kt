package com.jianglei.beautifulgirl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
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
import com.jianglei.beautifulgirl.data.WebsiteCenter
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.WebsiteVo
import kotlinx.android.synthetic.main.activity_all_website.*
import utils.ToastUtils

class AllWebsiteActivity : BaseActivity() {
    private var allWebsites: ArrayList<WebsiteVo>? = null
    private var dataSource: DataSource? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_website)
        rvWebsites.layoutManager = GridLayoutManager(this, 2)
        allWebsites = WebsiteCenter.getAllNormalWebsites()
        val adapter = WebsiteAdapter(this, allWebsites!!)
        adapter.onItemClickListener = object : OnItemClickListener<WebsiteVo> {
            override fun onItemClick(vo: WebsiteVo, pos: Int) {
                getPictureTypes(vo)
            }

        }
        rvWebsites.adapter = adapter

        bottomNav.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.action_in_wall -> {
                    allWebsites = WebsiteCenter.getAllNormalWebsites()
                    adapter.updateData(allWebsites!!)
                }
                else -> {
                    allWebsites = WebsiteCenter.getAllVpnWebsites()
                    adapter.updateData(allWebsites!!)
                }
            }
            true
        }
    }


    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            dataSource?.cancelAllNet()
        }
    }

    private fun getPictureTypes(vo: WebsiteVo) {
        dataSource = DataSourceCenter.getDataSource(vo.dataSourceKey)
        showProgress(true)
        dataSource?.fetAllTypes(vo.homePageUrl, object : OnDataResultListener<MutableList<Category>> {
            override fun onSuccess(data: MutableList<Category>) {
                data.forEach {
                    Log.d("jianglei", "获取分类:" + it.title + " " + it.url)
                }
                showProgress(false)
                if (data.size > 15) {
                    //分类数量大于15，要专门前往分类页面
                    val intent = Intent(this@AllWebsiteActivity, CategoryActivity::class.java)
                    intent.putExtra("dataSourceKey", vo.dataSourceKey)
                    intent.putExtra("websiteVo", vo)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@AllWebsiteActivity, ContentActivity::class.java)
                    intent.putParcelableArrayListExtra("types", data as java.util.ArrayList<out Parcelable>)
                    intent.putExtra("dataSourceKey", vo.dataSourceKey)
                    startActivity(intent)
                }
            }

            override fun onError(msg: String) {
                showProgress(false)
                ToastUtils.showMsg(this@AllWebsiteActivity, msg)
            }

        })

    }


    private inner class WebsiteAdapter(private var context: Context, private var websites: MutableList<WebsiteVo>) :
        RecyclerView.Adapter<WebsiteHolder>() {
        var onItemClickListener: OnItemClickListener<WebsiteVo>? = null

        fun updateData(websites: MutableList<WebsiteVo>) {
            this.websites = websites
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_all_websites, parent, false)
            return WebsiteHolder(view)
        }

        override fun getItemCount(): Int {
            return websites.size
        }

        override fun onBindViewHolder(holder: WebsiteHolder, position: Int) {
            holder.ivContent.setImageResource(websites[position].icon)
            holder.tvName.text = websites[position].name
            holder.tvType.text = websites[position].type
            holder.mainItem.setOnClickListener {
                onItemClickListener?.onItemClick(websites[position], position)
            }
        }


    }

    private class WebsiteHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivContent: ImageView = view.findViewById(R.id.iv_icon)
        var tvName: TextView = view.findViewById(R.id.name)
        var mainItem: CardView = view.findViewById(R.id.main_item)
        var tvType:TextView = view.findViewById(R.id.tv_type)
    }
}
