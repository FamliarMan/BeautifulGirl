package com.jianglei.girlshow

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.girlshow.storage.RuleRecord
import kotlinx.android.synthetic.main.activity_site_rule_list.*
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
            R.id.action_import_from_file -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, REQUEST_FILE)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            val url = data.data ?: return
            restoreFromFile(url)

        }
    }

    private fun writeRuleToFile() {
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

    fun restoreFromFile(uri: Uri) {
        showProgress(true)
        doAsync {
            var bfw: BufferedReader? = null
            try {
                val inputStream = contentResolver.openInputStream(uri)
                    ?: return@doAsync
                if (inputStream.available() > 1024 * 1024) {
                    uiThread {
                        showProgress(false)
                        DialogUtils.showLogTipDialog(
                            this@SiteRuleListActivity,
                            getString(R.string.restore_error)
                        )
                    }
                    return@doAsync
                }
                bfw = BufferedReader(InputStreamReader(inputStream))
                val rules = mutableListOf<RuleRecord>()
                while (true) {
                    val str = bfw.readLine() ?: break
                    if (str.isEmpty()) {
                        continue
                    }
                    val rule = JsonUtils.parseJsonWithGson(str, RuleRecord::class.java) ?: continue
                    rules.add(rule)

                }

                uiThread {
                    showProgress(false)
                    if (rules.isEmpty()) {
                        toast(R.string.import_none)
                    } else {
                        val restoreDialog = RestoreDialogFragment()
                        restoreDialog.setRecords(rules)
                        restoreDialog.show(
                            supportFragmentManager,
                            "restore"
                        )
                    }
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                uiThread {
                    showProgress(false)
                    DialogUtils.showLogTipDialog(
                        this@SiteRuleListActivity,
                        getString(R.string.restore_error)
                    )
                }
            } finally {
                bfw?.close()
            }

        }
    }

    companion object {
        private const val REQUEST_FILE = 100
    }
}
