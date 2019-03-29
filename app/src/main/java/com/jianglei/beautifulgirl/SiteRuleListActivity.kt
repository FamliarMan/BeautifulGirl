package com.jianglei.beautifulgirl

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.beautifulgirl.storage.RuleRecord
import kotlinx.android.synthetic.main.activity_site_rule_list.*
import utils.DialogUtils

class SiteRuleListActivity :BaseActivity() {
    private var adapter: RuleAdapter? = null
    private lateinit var ruleViewModel: RuleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_rule_list)
        rvRules.layoutManager = LinearLayoutManager(this)
        ruleViewModel = ViewModelProviders.of(this)
            .get(RuleViewModel::class.java)
        ruleViewModel.getRuleRecodeData()
            .observe(this, Observer {
                if (adapter == null) {
                    adapter = RuleAdapter(this@SiteRuleListActivity, it)
                    rvRules.adapter = adapter!!
                } else {
                    adapter!!.replaceAll(it)
//                    if (it.size != adapter!!.itemCount) {
//                    } else {
//                        adapter!!.notifyDataSetChanged()
//                    }
                }
            })
    }

    inner class RuleAdapter(val context: Context, val rules: List<RuleRecord>) :
        CommonRecyclerAdapter<RuleRecord>(context, R.layout.listitem_rule, rules) {
        override fun onUpdate(helper: BaseAdapterHelper?, item: RuleRecord?, position: Int) {
            helper?.setText(R.id.tvName, item?.name)
            helper?.setChecked(R.id.cbEnable, item?.enabled == 1)
            val checkBox = helper?.getView<CheckBox>(R.id.cbEnable)
            checkBox?.setOnCheckedChangeListener { p0, p1 ->
                if (item != null) {
                    item.enabled = if (p1) {
                        1
                    } else {
                        0
                    }
                    ruleViewModel.updateRule(item)
                }

            }
            helper?.setOnClickListener(R.id.ivDelete) {
                DialogUtils.showClickDialog(context,
                    context.getString(R.string.delete_confirm),
                    context.getString(R.string.confirm),
                    DialogInterface.OnClickListener { p0, p1 ->
                        ruleViewModel.deleteRule(item!!)
                    })
            }
            helper?.setOnClickListener(R.id.ivEdit) {
                val intent = Intent(context, SiteRuleEditActivity::class.java)
                intent.putExtra("rule", item)
                context.startActivity(intent)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rule_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_add_rule -> {
                val intent = Intent(this, SiteRuleEditActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}
