package com.jianglei.girlshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.girlshow.storage.RuleRecord

/**
 * @author jianglei on 3/30/19.
 */
class RestoreDialogFragment : DialogFragment() {
    val ruleRecords: MutableList<RuleRecord> = mutableListOf()
    fun setRecords(rules: List<RuleRecord>) {
        ruleRecords.clear()
        ruleRecords.addAll(rules)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_restore, container, false)
        val rvRestore = view.findViewById<RecyclerView>(R.id.rvRestore)
        rvRestore.layoutManager = LinearLayoutManager(activity!!)
        rvRestore.adapter = RestoreAdapter()
        val btn = view.findViewById<Button>(R.id.btnRestore)
        btn.setOnClickListener {
            val ruleViewModel = ViewModelProviders.of(activity!!)
                .get(RuleViewModel::class.java)
            for (rule in ruleRecords) {
                if (rule.enabled == 0) {
                    continue
                }
                rule.enabled = 1
//                Log.d("longyi","add rule:"+rule.name)
                ruleViewModel.addRule(rule)
            }
            dismiss()
        }

        return view
    }

    inner class RestoreAdapter : CommonRecyclerAdapter<RuleRecord>(activity!!, R.layout.listitem_rule, ruleRecords) {
        override fun onUpdate(helper: BaseAdapterHelper?, item: RuleRecord?, position: Int) {
            helper?.setVisible(R.id.ivEdit, false)
            helper?.setVisible(R.id.ivDelete, false)
            helper?.setText(R.id.tvName,item?.name)
            helper?.setChecked(R.id.cbEnable,item?.enabled ==1)

            helper?.getView<CheckBox>(R.id.cbEnable)?.setOnCheckedChangeListener { _, b ->
                //这里临时使用enable来记录被选中状态，真正导入的时候都会被置为1
                if (b) {
                    item?.enabled = 1
                } else {
                    item?.enabled = 0
                }
            }
        }


    }
}