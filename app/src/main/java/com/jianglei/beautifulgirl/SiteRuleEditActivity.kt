package com.jianglei.beautifulgirl

import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.databinding.ActivitySiteRuleEditBinding
import com.jianglei.beautifulgirl.rule.*
import com.jianglei.beautifulgirl.storage.RuleRecord
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.SearchVideoKeyWord
import com.jianglei.ruleparser.GsonUtil
import com.jianglei.ruleparser.RuleKeyWord
import com.jianglei.videoplay.ContentVo
import com.uuzuche.lib_zxing.activity.CaptureActivity
import kotlinx.android.synthetic.main.activity_site_rule_edit.*
import kotlinx.android.synthetic.main.activity_site_rule_edit.view.*
import utils.DialogUtils
import utils.JsonUtils
import android.widget.Toast
import com.jianglei.permission.JlPermission
import com.jianglei.permission.OnPermissionResultListener
import com.uuzuche.lib_zxing.activity.CodeUtils
import java.util.jar.Manifest


class SiteRuleEditActivity : BaseActivity() {

    private lateinit var binding: ActivitySiteRuleEditBinding
    private lateinit var webRule: WebRule
    private val REQUEST_FROM_QRCODE = 100
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_site_rule_edit, null, false)
        super.setContentView(binding.root)
        val caoliu = "{\n" +
                "  \"type\": \"video\",\n" +
                "  \"name\": \"XVideos\",\n" +
                "  \"icon\": \"https://www.xvideos.com/favicon.ico\",\n" +
                "  \"encoding\": \"utf-8\",\n" +
                "  \"url\": \"https://www.xvideos.com/channels-index\",\n" +
                "  \"categoryRule\": {\n" +
                "    \"dynamicRender\": true,\n" +
                "    \"nameRule\": \"@class:<thumb-block > -> @class:<profile-name>[0] ->@label:<a>[0]->@text\",\n" +
                "    \"urlRule\": \"@class:<thumb-block >->@class:<thumb>[0]->  @label:<a>[0]->@property:<href>\",\n" +
                "    \"imageUrlRule\": \"@class:<thumb-block >->@class:<thumb>[0]->@label:<a>[0]->@label:<img>[0]->@property:<src> \",\n" +
                "    \"descRule\": \"@class:<thumb-block >-> @class:<profile-counts>[0]->@text\",\n" +
                "    \"supportPage\":true,\n" +
                "    \"pageRule\": {\n" +
                "      \"fromHtml\": \"true\",\n" +
                "      \"startPage\": \"0\",\n" +
                "      \"nextUrlRule\": \"@class:<pagination>[0]->@label:<li>->@hasClass:<no-page next-page>->@label:<a>[0]->@property:<href>\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"coverRule\": {\n" +
                "    \"dynamicRender\": false,\n" +
                "    \"nameRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@text\",\n" +
                "    \"descRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@class:<metadata>[0]->@text\",\n" +
                "    \"imageUrlRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-inside>[0]->@class:<thumb>->@label:<img>[0]->@property:<data-src>\",\n" +
                "    \"urlRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@property:<href>\",\n" +
                "    \"realRequestUrlRule\": \"{baseUrl}/activity\",\n" +
                "    \"supportPage\":true,\n" +
                "    \"pageRule\": {\n" +
                "      \"fromHtml\": false,\n" +
                "      \"combinedUrl\": \"{baseUrl}/{page}\",\n" +
                "      \"paramRule\": \"@regex:<<!--[\\\\s*]([\\\\d]{10})[\\\\s*][-]{2}>[1]\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"searchRule\": {\n" +
                "    \"searchUrl\": \"https://www.xvideos.com/?k={searchTxt}&top\",\n" +
                "    \"resultRule\": {\n" +
                "      \"dynamicRender\": false,\n" +
                "      \"nameRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@text\",\n" +
                "      \"descRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@class:<metadata>[0]->@text\",\n" +
                "      \"imageUrlRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-inside>[0]->@class:<thumb>->@label:<img>[0]->@property:<data-src>\",\n" +
                "      \"urlRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@property:<href>\",\n" +
                "      \"realRequestUrlRule\": \"{baseUrl}/activity\",\n" +
                "      \"pageRule\": {\n" +
                "        \"fromHtml\": \"true\",\n" +
                "        \"startPage\": \"1\",\n" +
                "        \"nextUrlRule\": \"@class:<pagination>[0]->@label:<li>->@label:<a>[0]->@hasText:<{page}>->@property:<href>\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"suggestUrl\": \"https://www.xvideos.com/search-suggest/{searchTxt}\",\n" +
                "    \"suggestKeyRule\": \"@jsonArr:<KEYWORDS>->@jsonValue:<N>\",\n" +
                "    \"suggestTimeRule\": \"@jsonArr:<KEYWORDS>->@jsonValue:<R>\"\n" +
                "  },\n" +
                "  \"contentRule\": {\n" +
                "    \"dynamicRender\": true,\n" +
                "    \"urlRule\": \"@regex:<setVideoUrlHigh\\\\('(.*?)'\\\\)>[1]\",\n" +
                "    \"supportPage\":false\n" +
                "  }\n" +
                "}\n"
        initView()
        ruleRecord = intent.getParcelableExtra("rule")
        if (ruleRecord == null) {
            webRule = WebRule()
        } else {
            webRule = JsonUtils.parseJsonWithGson(ruleRecord!!.rule, WebRule::class.java)!!
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
            R.id.action_add_from_qrcode -> {
                JlPermission.start(this@SiteRuleEditActivity)
                    .permission(android.Manifest.permission.CAMERA)
                    .build()
                    .request(object : OnPermissionResultListener {
                        override fun onGranted(permissions: Array<out String>?) {
                            val intent = Intent(this@SiteRuleEditActivity, CaptureActivity::class.java)
                            startActivityForResult(intent, REQUEST_FROM_QRCODE)

                        }

                        override fun onDenied(permissions: Array<out String>?) {
                        }
                    })
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FROM_QRCODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            val bundle = data.extras ?: return
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                val result = bundle.getString(CodeUtils.RESULT_STRING)
                replaceRule(result)
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                DialogUtils.showTipDialog(this@SiteRuleEditActivity, "解析二维码失败")
            }
        }
    }

    private fun replaceRule(json: String?) {
        if (json == null) {
            DialogUtils.showTipDialog(this@SiteRuleEditActivity, getString(R.string.error_json))
            return
        }
        val newWebRule = JsonUtils.parseJsonWithGson(json, WebRule::class.java)
        if (newWebRule == null) {
            DialogUtils.showTipDialog(this@SiteRuleEditActivity, getString(R.string.error_json))
            return
        }
        webRule = newWebRule
        bindRule(webRule)
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
        if (webRule.searchRule != null) {
            binding.searchPage = webRule.searchRule!!.resultRule!!.pageRule
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
        if (webStrategy == null) {
            webStrategy = WebStrategy(webRule)
        }
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
        if (webStrategy == null) {
            webStrategy = WebStrategy(webRule)
        }
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
            })
    }

}
