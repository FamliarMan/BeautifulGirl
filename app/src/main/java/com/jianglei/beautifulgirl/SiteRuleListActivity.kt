package com.jianglei.beautifulgirl

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.beautifulgirl.storage.DataStorage
import com.jianglei.beautifulgirl.storage.RuleRecord
import kotlinx.android.synthetic.main.activity_site_rule_list.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import utils.DialogUtils
import utils.JsonUtils
import java.io.*

class SiteRuleListActivity : BaseActivity() {
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
            R.id.action_export_to_file -> {
                writeRuleToFile()
                true
            }
//            R.id.action_import_from_file->{
//                restoreFromFile()
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun writeRuleToFile() {
        val ref = this.asReference()
        showProgress(true)
        doAsync {
            val dir = Environment.getExternalStorageDirectory().toString() + "/BeautifulGirl"
            val file = File(dir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val path = "$dir/rule.bak"
            var bfw: BufferedWriter? = null
            try {
                bfw = BufferedWriter(FileWriter(File(path)))
                for (ruleRecord in adapter!!.data) {
                    bfw.write(JsonUtils.toJsonString(ruleRecord).replace("\n", ""))
                    bfw.write("\n\n")
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                uiThread {
                    toast(R.string.backup_error)
                }
            } finally {
                bfw?.close()
                uiThread {
                    showProgress(false)
                    toast(getString(R.string.backup_success, path))
                }
            }

        }

    }

    fun restoreFromFile(){
        doAsync {
            val dir = Environment.getExternalStorageDirectory().toString() + "/BeautifulGirl"
            val file = File(dir)
            if (!file.exists()) {
                toast(R.string.restore_not_exist)
                return@doAsync
            }
            val path = "$dir/rule.bak"
            var bfw: BufferedReader? = null
            try {
                bfw = BufferedReader(FileReader(File(path)))
                while(true){
                    val str = bfw.readLine() ?: break
                    if(str.isEmpty()){
                        continue
                    }
                    Log.d("longyi",str)

                }

            } catch (e: Throwable) {
                e.printStackTrace()
                uiThread {
                    toast(R.string.backup_error)
                }
            } finally {
                bfw?.close()
                uiThread {
                    showProgress(false)
                    toast(getString(R.string.backup_success, path))
                }
            }

        }
    }
}
