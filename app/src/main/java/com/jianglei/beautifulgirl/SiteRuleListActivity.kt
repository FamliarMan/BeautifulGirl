package com.jianglei.beautifulgirl

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonAdapter
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.beautifulgirl.rule.WebRule
import com.jianglei.beautifulgirl.storage.RuleRecord
import kotlinx.android.synthetic.main.activity_site_rule_list.*

class SiteRuleListActivity : AppCompatActivity() {
    private var adapter: RuleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_rule_list)
        rvRules.layoutManager = LinearLayoutManager(this)
        ViewModelProviders.of(this)
            .get(RuleViewModel::class.java)
            .getRuleRecodeData()
            .observe(this, Observer {
                if (adapter == null) {
                    adapter = RuleAdapter(this@SiteRuleListActivity, it)
                    rvRules.adapter = adapter!!
                } else {
                    adapter!!.replaceAll(it)
                }
            })
    }

    class RuleAdapter(context: Context, rules: List<RuleRecord>) :
        CommonRecyclerAdapter<RuleRecord>(context, R.layout.listitem_rule, rules) {
        override fun onUpdate(helper: BaseAdapterHelper?, item: RuleRecord?, position: Int) {
            helper?.setText(R.id.tvName, item?.name)
            helper?.setChecked(R.id.cbEnable, item?.enabled == 1)
        }

    }


}
