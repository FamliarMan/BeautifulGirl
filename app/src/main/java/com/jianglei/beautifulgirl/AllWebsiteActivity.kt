package com.jianglei.beautifulgirl

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.rule.RuleCenter
import com.jianglei.beautifulgirl.rule.RuleConstants
import com.jianglei.beautifulgirl.rule.WebRule
import com.jianglei.beautifulgirl.rule.WebStrategy
import com.jianglei.beautifulgirl.storage.DataStorage
import com.jianglei.beautifulgirl.storage.OnSqlExcuteListener
import com.jianglei.beautifulgirl.storage.RuleRecord
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.permission.JlPermission
import com.jianglei.permission.OnPermissionResultListener
import com.jianglei.ruleparser.LogUtil
import com.jianglei.ruleparser.ruledescription.RuleDesc
import kotlinx.android.synthetic.main.activity_all_website.*
import kotlinx.android.synthetic.main.activity_base.*
import utils.DialogUtils
import utils.JsonUtils
import utils.ToastUtils

class AllWebsiteActivity : BaseActivity() {
    private var videoRules: MutableList<WebRule> = mutableListOf()
    private var imageRules: MutableList<WebRule> = mutableListOf()
    private var curWebStrategy: WebStrategy? = null
    private lateinit var adapter:WebsiteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_website)
        initLog()
        initView()
        initData()
        rvWebsites.layoutManager = GridLayoutManager(this, 2)
        adapter = WebsiteAdapter(this, videoRules)
        adapter.onItemClickListener = object : OnItemClickListener<WebRule> {
            override fun onItemClick(vo: WebRule, pos: Int) {
                getCategory(vo)
            }

        }
        rvWebsites.adapter = adapter

        bottomNav.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.action_video -> {
                    adapter.updateData(videoRules)
                }
                else -> {
                    adapter.updateData(imageRules)
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

    private fun initView() {
        navBody.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_site -> {
                    //站点管理
                    val intent = Intent(this, SiteRuleListActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_study -> {
                    val intent = Intent(this, RuleHelpActivity::class.java)
                    startActivity(intent)

                }
            }
            true

        }
        //初始化侧边栏
        val drawerToggle = ActionBarDrawerToggle(
            this,
            navSlide,
            toolBar,
            R.string.open_draw,
            R.string.close_draw
        )
        navSlide.addDrawerListener(drawerToggle)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.mipmap.ic_func))

    }

    private fun initData() {
        val vm = ViewModelProviders.of(this).get(RuleViewModel::class.java)
        vm.getRuleData().observe(this, Observer<List<WebRule>> {
            imageRules.clear()
            videoRules.clear()
            for (rule in it) {
                if(rule.type ==RuleConstants.TYPE_IMAGE){
                    imageRules.add(rule)
                }else{
                    videoRules.add(rule)
                }
                adapter.notifyDataSetChanged()
            }
        })
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
                    DialogUtils.showLogTipDialog(this@AllWebsiteActivity, msg)
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
