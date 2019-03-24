package com.jianglei.beautifulgirl

import android.Manifest
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
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.rule.RuleCenter
import com.jianglei.beautifulgirl.rule.WebRule
import com.jianglei.beautifulgirl.rule.WebStrategy
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.permission.JlPermission
import com.jianglei.permission.OnPermissionResultListener
import com.jianglei.ruleparser.LogUtil
import kotlinx.android.synthetic.main.activity_all_website.*
import utils.ToastUtils

class AllWebsiteActivity : BaseActivity() {
    private var allWebsites: ArrayList<WebDataSource>? = null
    private var webDataSource: WebDataSource? = null
    private var webRules = RuleCenter.getWebRules()
    private var curWebStrategy: WebStrategy? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_website)
        initLog()
        rvWebsites.layoutManager = GridLayoutManager(this, 2)
        allWebsites = WebSourceCenter.normalWebSources
        val adapter = WebsiteAdapter(this, webRules as MutableList<WebRule>)
        adapter.onItemClickListener = object : OnItemClickListener<WebRule> {
            override fun onItemClick(vo: WebRule, pos: Int) {
                getCategory(vo)
            }

        }
        rvWebsites.adapter = adapter

        bottomNav.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.action_in_wall -> {
                    allWebsites = WebSourceCenter.normalWebSources
//                    adapter.updateData(allWebsites!!)
                }
                else -> {
                    allWebsites = WebSourceCenter.vpnWebSources
//                    adapter.updateData(allWebsites!!)
                }
            }
            true
        }
    }


    private fun initLog() {
        JlPermission.start(this)
            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .build()
            .request(object : OnPermissionResultListener {
                override fun onGranted(permissions: Array<out String>?) {
                    LogUtil.init(true, true, true)
                }

                override fun onDenied(permissions: Array<out String>?) {
                    LogUtil.init(true, false, false)
                }
            })
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            webDataSource?.cancelAllNet()
        }
    }

    private fun getCategory(vo: WebRule) {
        showProgress(true)
        curWebStrategy = WebStrategy(vo)
        StrategyProvider.updateCurStrategy(curWebStrategy!!)
        curWebStrategy!!.fetchAllCategory(this,
            1,
            object : OnDataResultListener<List<Category>> {
                override fun onSuccess(data: List<Category>) {
                    showProgress(false)
                    if (data.size >= 8) {
                        //分类数量大于15，要专门前往分类页面
                        val intent = Intent(this@AllWebsiteActivity, CategoryActivity::class.java)
//                        intent.putExtra("dataSourceId", vo.id)
                        startActivity(intent)
                    } else {
                        ContentActivity.start(this@AllWebsiteActivity, data)
                    }
                }

                override fun onError(msg: String) {
                    showProgress(false)
                    ToastUtils.showMsg(this@AllWebsiteActivity, msg)
                }
            })

    }


    private inner class WebsiteAdapter(private var context: Context, private var webRules: MutableList<WebRule>) :
        RecyclerView.Adapter<WebsiteHolder>() {
        var onItemClickListener: OnItemClickListener<WebRule>? = null

        fun updateData(webRules: MutableList<WebRule>) {
            this.webRules = webRules
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_all_websites, parent, false)
            return WebsiteHolder(view)
        }

        override fun getItemCount(): Int {
            return webRules.size
        }

        override fun onBindViewHolder(holder: WebsiteHolder, position: Int) {
            holder.ivContent.setImageURI(webRules[position].icon)
            holder.tvName.text = webRules[position].name
            holder.tvType.text = webRules[position].type
            holder.mainItem.setOnClickListener {
                onItemClickListener?.onItemClick(webRules[position], position)
            }
        }


    }

    private class WebsiteHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivContent: SimpleDraweeView = view.findViewById(R.id.iv_icon)
        var tvName: TextView = view.findViewById(R.id.name)
        var mainItem: CardView = view.findViewById(R.id.main_item)
        var tvType: TextView = view.findViewById(R.id.tv_type)
    }
}
