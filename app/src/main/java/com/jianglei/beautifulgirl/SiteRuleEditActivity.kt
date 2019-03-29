package com.jianglei.beautifulgirl

import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.databinding.ActivitySiteRuleEditBinding
import com.jianglei.beautifulgirl.rule.*
import com.jianglei.beautifulgirl.storage.RuleRecord
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.SearchVideoKeyWord
import com.jianglei.ruleparser.LogUtil
import com.jianglei.ruleparser.RuleKeyWord
import com.jianglei.videoplay.ContentVo
import kotlinx.android.synthetic.main.activity_site_rule_edit.*
import utils.DialogUtils
import utils.JsonUtils
import utils.ToastUtils


class SiteRuleEditActivity : BaseActivity() {

    private lateinit var binding: ActivitySiteRuleEditBinding
    private lateinit var webRule: WebRule
    private var webStrategy: WebStrategy? = null
    private var ruleRecord: RuleRecord? = null
    /**
     * 第一个一级分类的url，为二级分类调试准备
     */
    private var firstCategoryUrl: String? = null
    /**
     * 第一个二级分类的url，为具体地址调试做准备
     */
    private var firstCoveryUrl: String? = null
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_site_rule_edit, null, false)
        super.setContentView(binding.root)
        initView()
        ruleRecord = intent.getParcelableExtra("rule")
        if (ruleRecord == null) {
            webRule = getEmptyRule()
            isEdit = false
        } else {
            webRule = getRuleFromJson(ruleRecord!!.rule)!!
            isEdit = true
        }
        bindRule(webRule)
    }

    private fun initView() {
        binding.btnFirstDebug.setOnClickListener {
            debugCategory()

        }
        binding.btnSecondDebug.setOnClickListener {
            debugSecondCategory()
        }
        binding.btnContentDebug.setOnClickListener {
            debugContent()
        }
        btnSearchDebug.setOnClickListener {
            debugSearch()
        }
        btnSearchSuggestDebug.setOnClickListener {
            debugSearchSuggest()
        }

    }

    private fun getEmptyRule(): WebRule {
        val webRule = WebRule()
        //第一级分类规则
        val categoryPage = PageRule()
        val categoryRule = CategoryRule()
        categoryRule.pageRule = categoryPage

        //第二级分类规则
        val coverPage = PageRule()
        val coverRule = CategoryRule()
        coverRule.pageRule = coverPage

        //第三级分类规则
        val contentPage = PageRule()
        val contentRule = CategoryRule()
        contentRule.pageRule = contentPage

        //搜索规则
        val searchResultRule = CategoryRule()
        val searchResultPage = PageRule()
        searchResultRule.pageRule = searchResultPage
        val searchRule = SearchRule()
        searchRule.resultRule = searchResultRule

        webRule.searchRule = searchRule
        webRule.categoryRule = categoryRule
        webRule.coverRule = coverRule
        webRule.contentRule = contentRule
        return webRule
    }

    /**
     * 有些特殊情况json有些数据没有记载，我们需要补全，
     * 方便双向绑定，考虑这种情况，某个网站事先不支持搜索，
     * 所以我们searchRule字段为空，如果我们想重新支持搜索，
     * 我们双向绑定就会出问题，因为这个时候根本没有对应数据绑定
     * 所以我们要先补上一个空的searchRule,由于supportSearch还是没有
     * 改变，所以不会影响
     */
    private fun getCompleteRule(webRule: WebRule): WebRule {
        //补齐一级分类分页
        if (webRule.categoryRule!!.pageRule == null) {
            webRule.categoryRule!!.pageRule = PageRule()

        }
        //补齐二级分类分页
        if (webRule.coverRule!!.pageRule == null) {
            webRule.coverRule!!.pageRule = PageRule()
        }
        //补齐三级分类分页
        if (webRule.contentRule!!.pageRule == null) {
            webRule.contentRule!!.pageRule = PageRule()
        }
        //补齐搜索规则
        if (webRule.searchRule == null) {
            val searchResultRule = CategoryRule()
            val searchResultPage = PageRule()
            searchResultRule.pageRule = searchResultPage
            val searchRule = SearchRule()
            searchRule.resultRule = searchResultRule
            webRule.searchRule = searchRule
        } else if (webRule.searchRule!!.resultRule == null) {
            val searchResultRule = CategoryRule()
            val searchResultPage = PageRule()
            searchResultRule.pageRule = searchResultPage
            webRule.searchRule!!.resultRule = searchResultRule
        } else if (webRule.searchRule!!.resultRule!!.pageRule == null) {
            webRule.searchRule!!.resultRule!!.pageRule = PageRule()
        }
        return webRule
    }

    private fun getRuleFromJson(str: String): WebRule? {
        val webRule = JsonUtils.parseJsonWithGson(str, WebRule::class.java)
        if (webRule == null) {
            DialogUtils.showTipDialog(this, getString(R.string.error_json))
            return null
        }
        return getCompleteRule(webRule)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rule_commit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_save -> {
                save()
                true
            }
//            R.id.action_add_from_qrcode -> {
//                JlPermission.start(this@SiteRuleEditActivity)
//                    .permission(android.Manifest.permission.CAMERA)
//                    .build()
//                    .request(object : OnPermissionResultListener {
//                        override fun onGranted(permissions: Array<out String>?) {
//                            val intent = Intent(this@SiteRuleEditActivity, CaptureActivity::class.java)
//                            startActivityForResult(intent, REQUEST_FROM_QRCODE)
//
//                        }
//
//                        override fun onDenied(permissions: Array<out String>?) {
//                        }
//                    })
//                true
//            }
            R.id.action_add_from_clipboard -> {
                //从剪贴板导入
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val json = cm.primaryClip?.getItemAt(0)?.text.toString()
                val newRule = getRuleFromJson(json)
                if (newRule != null) {
                    webRule = newRule
                    bindRule(webRule)
                }
                true
            }
            R.id.action_export_to_clipboard -> {
                //导出到剪贴板
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", JsonUtils.toJsonString(webRule))
                cm.primaryClip = clip
                ToastUtils.showShortMsg(this, getString(R.string.copy_success))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_FROM_QRCODE && resultCode == Activity.RESULT_OK) {
//            if (data == null) {
//                return
//            }
//            val bundle = data.extras ?: return
//            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                val result = bundle.getString(CodeUtils.RESULT_STRING)
//                replaceRule(result)
//            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                DialogUtils.showTipDialog(this@SiteRuleEditActivity, "解析二维码失败")
//            }
//        }
    }


    private fun bindRule(webRule: WebRule) {
        binding.webRule = webRule
        binding.categoryPage = webRule.categoryRule!!.pageRule
        binding.categoryRule = webRule.categoryRule
        binding.coverRule = webRule.coverRule
        binding.coverPage = webRule.coverRule!!.pageRule
        binding.contentRule = webRule.contentRule
        binding.contentPage = webRule.contentRule!!.pageRule
        binding.searchRule = webRule.searchRule
        binding.searchPage = webRule.searchRule!!.resultRule!!.pageRule
    }

    /**
     * 调试第一级类别规则
     */
    private fun debugCategory() {
        firstCategoryUrl = null
        if (!checkFirstCategory()) {
            return
        }

        showProgress(true)
        webStrategy = WebStrategy(webRule)
        webStrategy!!.fetchAllCategory(this, 0, object : OnDataResultListener<List<Category>> {
            override fun onSuccess(data: List<Category>) {
                showProgress(false)
                if (data.isEmpty()) {
                    showLogTipDialog(getString(R.string.log_category_fail))
                    return
                }
                showLogTipDialog(
                    getString(
                        R.string.log_category_success,
                        data.size, webStrategy!!.nextCategoryUrl
                    )
                )
                firstCategoryUrl = data[0].url
            }

            override fun onError(msg: String) {
                showProgress(false)
                showLogTipDialog(msg)
            }
        })


    }

    /**
     * 调试第二级类别规则
     */
    private fun debugSecondCategory() {
        firstCoveryUrl = null
        if (!checkSecondCategory()) {
            return
        }
        if (firstCategoryUrl == null) {
            DialogUtils.showTipDialog(this, getString(R.string.please_debug_first_category))
            return
        }
        showProgress(true)
        webStrategy = WebStrategy(webRule)
        webStrategy!!.fetchAllCover(this, 1, firstCategoryUrl, false,
            object : OnDataResultListener<List<ContentTitle>> {
                override fun onSuccess(data: List<ContentTitle>) {
                    showProgress(false)
                    if (data.isEmpty()) {
                        showLogTipDialog(getString(R.string.log_category_fail))
                        return
                    }
                    showLogTipDialog(
                        getString(
                            R.string.log_category_success,
                            data.size, webStrategy!!.nextCoverUrl
                        )
                    )
                    firstCoveryUrl = data[0].detailUrl
                }

                override fun onError(msg: String) {
                    showProgress(false)
                    showLogTipDialog(msg)
                }
            })

    }

    private fun debugContent() {

        if (!checkContentInfo()) {
            return
        }
        if (firstCoveryUrl == null) {
            DialogUtils.showTipDialog(this, getString(R.string.please_debug_second_category))
            return
        }
        showProgress(true)
        webStrategy = WebStrategy(webRule)
        webStrategy!!.fetchAllContents(this, 1, firstCoveryUrl,
            object : OnDataResultListener<List<ContentVo>> {
                override fun onSuccess(data: List<ContentVo>) {
                    showProgress(false)
                    if (data.isEmpty()) {
                        showLogTipDialog(getString(R.string.log_content_fail))
                        return
                    }
                    showLogTipDialog(
                        getString(
                            R.string.log_content_success,
                            data.size, webStrategy!!.nextContentUrl
                        )
                    )
                }

                override fun onError(msg: String) {
                    showProgress(false)
                    showLogTipDialog(msg)
                }
            })
    }

    private fun debugSearchSuggest() {
        if (!checkSearchSuggest()) {
            return
        }
        val searchTxt = debugSearchSuggestTxt.getContent()
        if (searchTxt.isNullOrBlank()) {

            DialogUtils.showTipDialog(
                this, getString(
                    R.string.check_not_null,
                    getString(R.string.title_debug_searchtxt)
                )
            )
            return
        }
        webStrategy = WebStrategy(webRule)
        showProgress(true)
        webStrategy!!.fetchSearchSuggest(searchTxt, object : OnDataResultListener<List<SearchVideoKeyWord>> {
            override fun onSuccess(data: List<SearchVideoKeyWord>) {
                showProgress(false)
                showLogTipDialog(getString(R.string.log_search_suggest_success, data.size))
            }

            override fun onError(msg: String) {
                showProgress(false)
                showLogTipDialog(msg)
            }
        })
    }

    private fun debugSearch() {
        if (!checkSearchInfo()) {
            return
        }
        val searchTxt = debugSearchTxt.getContent()
        if (searchTxt.isNullOrBlank()) {

            DialogUtils.showTipDialog(
                this, getString(
                    R.string.check_not_null,
                    getString(R.string.title_debug_searchtxt)
                )
            )
            return
        }
        webStrategy = WebStrategy(webRule)
        showProgress(true)
        val url = webRule.searchRule!!.searchUrl.replace(RuleKeyWord.SEARCH_TXT, searchTxt)
        webStrategy!!.fetchAllCover(this,
            1, url, true,
            object : OnDataResultListener<List<ContentTitle>> {
                override fun onSuccess(data: List<ContentTitle>) {
                    showProgress(false)
                    showLogTipDialog(getString(R.string.log_search_success, data.size, webStrategy!!.nextCoverUrl))
                }

                override fun onError(msg: String) {
                    showProgress(false)
                    showLogTipDialog(msg)
                }
            })
    }


    private fun save() {
        if (!checkContentInfo()) {
            return
        }
        if (!checkSearchInfo()) {
            return
        }
        val ruleViewModel: RuleViewModel = ViewModelProviders.of(this).get(RuleViewModel::class.java)
        if (isEdit) {
            ruleRecord!!.rule = JsonUtils.toJsonString(webRule)
            ruleViewModel.updateRule(ruleRecord!!)

        } else {
            val ruleRecord = RuleRecord(0, webRule.name, 1, JsonUtils.toJsonString(webRule))
            ruleViewModel.addRule(ruleRecord)
        }

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

    private fun checkCategoryInfo(categoryRule: CategoryRule?, isLastLevel: Boolean = false): Boolean {
        if (categoryRule == null) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.first_rule_title)))
            return false
        }
        if (!isLastLevel && categoryRule.nameRule.isBlank()) {
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
        return checkSecondCategory() && checkCategoryInfo(webRule.contentRule, true)
    }

    private fun checkSearchInfo(): Boolean {
        if(!webRule.supportSearch){
            return true
        }
        if (webRule.searchRule!!.searchUrl.isBlank()) {
            DialogUtils.showTipDialog(this, getString(R.string.check_not_null, getString(R.string.title_searchUrl)))
            return false
        }
        return checkSearchSuggest() && checkCategoryInfo(webRule.searchRule!!.resultRule)
    }

    private fun checkSearchSuggest(): Boolean {
        if (webRule.searchRule!!.supportSuggest) {
            if (webRule.searchRule!!.suggestUrl.isNullOrBlank()) {
                DialogUtils.showTipDialog(
                    this, getString(
                        R.string.check_not_null,
                        getString(R.string.title_searchSuggestUrl)
                    )
                )
                return false
            }
            if (webRule.searchRule!!.suggestKeyRule.isNullOrBlank()) {
                DialogUtils.showTipDialog(
                    this, getString(
                        R.string.check_not_null,
                        getString(R.string.title_searchSuggestKeyRule)
                    )
                )
                return false
            }

        }
        return true
    }

    private fun showLogTipDialog(msg: String) {
        DialogUtils.showClickDialog(this, msg, getString(R.string.check_detail_log),
            DialogInterface.OnClickListener { dialog, which ->
                LogUtil.openLog(this@SiteRuleEditActivity)
            })
    }


}
