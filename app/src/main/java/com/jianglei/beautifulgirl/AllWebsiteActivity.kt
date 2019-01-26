package com.jianglei.beautifulgirl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.vo.Category
import kotlinx.android.synthetic.main.activity_all_website.*
import utils.ToastUtils

class AllWebsiteActivity : BaseActivity() {
    private var allWebsites: ArrayList<WebDataSource>? = null
    private var webDataSource: WebDataSource? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_website)
        rvWebsites.layoutManager = GridLayoutManager(this, 2)
        allWebsites = WebSourceCenter.normalWebSources
        val adapter = WebsiteAdapter(this, allWebsites!!)
        adapter.onItemClickListener = object : OnItemClickListener<WebDataSource> {
            override fun onItemClick(vo: WebDataSource, pos: Int) {
                getPictureTypes(vo)
            }

        }
        rvWebsites.adapter = adapter

        bottomNav.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.action_in_wall -> {
                    allWebsites = WebSourceCenter.normalWebSources
                    adapter.updateData(allWebsites!!)
                }
                else -> {
                    allWebsites = WebSourceCenter.vpnWebSources
                    adapter.updateData(allWebsites!!)
                }
            }
            true
        }
    }


    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            webDataSource?.cancelAllNet()
        }
    }

    private fun getPictureTypes(vo: WebDataSource) {
        val websiteDesc = vo.fetchWebsite()
        webDataSource = vo
        showProgress(true)
        webDataSource?.fetchAllCategory(websiteDesc.homePageUrl, object : OnDataResultListener<MutableList<Category>> {
            override fun onSuccess(data: MutableList<Category>) {
                data.forEach {
                    Log.d("jianglei", "获取分类:" + it.title + " " + it.url)
                }
                showProgress(false)
                if (data.size > 15) {
                    //分类数量大于15，要专门前往分类页面
                    val intent = Intent(this@AllWebsiteActivity, CategoryActivity::class.java)
                    intent.putExtra("dataSourceId", vo.id)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@AllWebsiteActivity, ContentActivity::class.java)
                    intent.putParcelableArrayListExtra("types", data as java.util.ArrayList<out Parcelable>)
                    intent.putExtra("dataSourceId", vo.id)
                    startActivity(intent)
                }
            }

            override fun onError(msg: String) {
                showProgress(false)
                ToastUtils.showMsg(this@AllWebsiteActivity, msg)
            }

        })

    }


    private inner class WebsiteAdapter(private var context: Context, private var websites: MutableList<WebDataSource>) :
        RecyclerView.Adapter<WebsiteHolder>() {
        var onItemClickListener: OnItemClickListener<WebDataSource>? = null

        fun updateData(websites: MutableList<WebDataSource>) {
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
            val websiteDesc = websites[position].fetchWebsite()
            holder.ivContent.setImageResource(websiteDesc.icon)
            holder.tvName.text = websiteDesc.name
            holder.tvType.text = websiteDesc.type
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
