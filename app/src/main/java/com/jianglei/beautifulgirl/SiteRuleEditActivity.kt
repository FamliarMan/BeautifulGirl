package com.jianglei.beautifulgirl

import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.databinding.ActivitySiteRuleEditBinding
import com.jianglei.beautifulgirl.rule.*
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.ruleparser.GsonUtil
import com.jianglei.videoplay.ContentVo
import utils.DialogUtils
import utils.JsonUtils

class SiteRuleEditActivity : BaseActivity() {

    private lateinit var binding: ActivitySiteRuleEditBinding
    private lateinit var webRule: WebRule
    private lateinit var webStrategy: WebStrategy
    /**
     * 第一个一级分类的url，为二级分类调试准备
     */
    private var firstCategoryUrl: String? = null
    /**
     * 第一个二级分类的url，为具体地址调试做准备
     */
    private var firstCoveryUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_site_rule_edit, null, false)
        super.setContentView(binding.root)
        initView()
        val categoryPageRule = PageRule()
        categoryPageRule.fromHtml = true
        categoryPageRule.nextUrlRule = "@class"

        val coverPageRule = PageRule()
        coverPageRule.fromHtml = false
        coverPageRule.combinedUrl = "{baseUrl}&page={page}"
        coverPageRule.startPage = 0
        val categoryRule = CategoryRule()
        categoryRule.dynamicRender = true
        categoryRule.pageRule = categoryPageRule
        categoryRule.nameRule = "@class"
        categoryRule.urlRule = "@class"

        val coverRule = CategoryRule()
        categoryRule.dynamicRender = true
        categoryRule.nameRule = "@class"
        categoryRule.urlRule = "@class"

        val contentPage = PageRule()
        val contentRule = CategoryRule()
        contentRule.dynamicRender = true
        contentRule.nameRule = "@class"
        contentRule.supportPage = true
        webRule = WebRule()
        webRule.categoryRule = categoryRule
        webRule.coverRule = coverRule
        webRule.contentRule = contentRule
        webRule.name = "测试网站"
        webRule.url = "www.baidu.com"
        webRule.icon = "j"
        webRule.type = "image"
        webRule.encoding = "GBK"
        binding.webRule = webRule
        binding.categoryPage = categoryPageRule
        binding.categoryRule = categoryRule
        binding.coverRule = coverRule
        binding.coverPage = coverPageRule
        binding.contentRule = contentRule
        binding.contentPage = contentPage
    }

    private fun initView() {
        binding.btnFirstDebug.setOnClickListener {
            checkFirstCategory()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rule_commit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_save -> {

                Log.d("longyi", GsonUtil.gson.toJson(webRule))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    /**
     * 调试第一级类别规则
     */
    fun debugCategory() {
        firstCategoryUrl = null
        if (!checkFirstCategory()) {
            return
        }

        webStrategy = WebStrategy(webRule)
        webStrategy.fetchAllCategory(this, 0, object : OnDataResultListener<List<Category>> {
            override fun onSuccess(data: List<Category>) {
                if (data.isEmpty()) {
                    showLogTipDialog(getString(R.string.log_category_fail))
                    return
                }
                showLogTipDialog(
                    getString(
                        R.string.log_category_success,
                        data.size, webStrategy.nextCategoryUrl
                    )
                )
                firstCategoryUrl = data[0].url
            }

            override fun onError(msg: String) {
                showLogTipDialog(msg)
            }
        })


    }

    /**
     * 调试第二级类别规则
     */
    fun debugSecondCategory() {
        firstCoveryUrl = null
        if (!checkSecondCategory()) {
            return
        }
        if (firstCategoryUrl == null) {
            DialogUtils.showTipDialog(this, getString(R.string.please_debug_first_category))
            return
        }
        webStrategy.fetchAllCover(this, 1, firstCategoryUrl, false,
            object : OnDataResultListener<List<ContentTitle>> {
                override fun onSuccess(data: List<ContentTitle>) {
                    if (data.isEmpty()) {
                        showLogTipDialog(getString(R.string.log_category_fail))
                        return
                    }
                    showLogTipDialog(
                        getString(
                            R.string.log_category_success,
                            data.size, webStrategy.nextCoverUrl
                        )
                    )
                    firstCoveryUrl = data[0].detailUrl
                }

                override fun onError(msg: String) {
                    showLogTipDialog(msg)
                }
            })

    }

    fun debugContent() {

        if (!checkContentInfo()) {
            return
        }
        if (firstCoveryUrl == null) {
            DialogUtils.showTipDialog(this, getString(R.string.please_debug_second_category))
            return
        }
        webStrategy.fetchAllContents(this, 1, firstCoveryUrl,
            object : OnDataResultListener<List<ContentVo>> {
                override fun onSuccess(data: List<ContentVo>) {
                    if (data.isEmpty()) {
                        showLogTipDialog(getString(R.string.log_content_fail))
                        return
                    }
                    showLogTipDialog(
                        getString(
                            R.string.log_content_success,
                            data.size, webStrategy.nextContentUrl
                        )
                    )
                }

                override fun onError(msg: String) {
                    showLogTipDialog(msg)
                }
            })
    }

    private fun checkWebBaseInfo(): Boolean {
        if (webRule.name.isEmpty()) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.title_web_name)))
            return false
        }
        if (webRule.url.isEmpty()) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.title_web_url)))
            return false
        }
        return true
    }

    private fun checkCategoryInfo(categoryRule: CategoryRule?): Boolean {
        if (categoryRule == null) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.first_rule_title)))
            return false
        }
        if (categoryRule.nameRule.isBlank()) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.category_name_rule)))
            return false

        }
        if (categoryRule.urlRule.isBlank()) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.category_url_rule)))
            return false
        }
        if (categoryRule.supportPage) {
            //支持分页需要检查分页设置
            if (categoryRule.pageRule == null) {
                DialogUtils.showTipDialog(
                    this,
                    getString(R.string.check_not_null, getString(R.string.first_category_page))
                )
                return false
            }
            if (categoryRule.pageRule!!.fromHtml && categoryRule.pageRule!!.nextUrlRule.isNullOrBlank()) {
                DialogUtils.showTipDialog(
                    this,
                    getString(R.string.check_not_null, getString(R.string.title_nextUrlRule))
                )
                return false
            }
            if (!categoryRule.pageRule!!.fromHtml
                && categoryRule.pageRule!!.combinedUrl.isNullOrBlank()
            ) {
                DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.title_paramRule)))
                return false
            }

        }
        return true
    }

    /**
     * 检查第一级分类信息
     */
    private fun checkFirstCategory(): Boolean {
        if (!checkWebBaseInfo()) {
            return false
        }
        return checkCategoryInfo(webRule.categoryRule)

    }

    private fun checkSecondCategory(): Boolean {
        return checkFirstCategory() && checkCategoryInfo(webRule.coverRule)
    }

    private fun checkContentInfo(): Boolean {
        return checkSecondCategory() && checkCategoryInfo(webRule.contentRule)
    }

    private fun showLogTipDialog(msg: String) {
        DialogUtils.showClickDialog(this, msg, getString(R.string.check_detail_log),
            DialogInterface.OnClickListener { dialog, which ->
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            })
    }

}
