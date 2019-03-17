package com.jianglei.beautifulgirl.rule

import androidx.fragment.app.FragmentActivity
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.OnWebViewResultListener
import com.jianglei.beautifulgirl.data.WebGetter
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.ruleparser.RuleParser
import org.jsoup.Jsoup
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * @author jianglei on 3/17/19.
 */
class WebStrategy(private val webRule: WebRule) {
    private var webGetter: WebGetter = WebGetter()
    /**
     * 获取某个网站的所有分类栏目
     */
    fun fetchAllCategory(
        activity: FragmentActivity,
        listener: OnDataResultListener<MutableList<Category>>,
        page: Int = 1
    ) {
        var realUrl:String = webRule.categoryRule.url
        if(webRule.categoryRule.pageRule != null){
            //分类页面支持分页
            if(webRule.categoryRule.pageRule!!.pageHolder == null){
                throw IllegalArgumentException("分类里面的分页规则缺少页码占位符")
            }
            realUrl = webRule.categoryRule.url.replace(
                webRule.categoryRule.pageRule!!.pageHolder!!,
                (page -1 +webRule.categoryRule.pageRule!!.startPage).toString()
            )
        }
        webGetter.getWebsiteHtml(
            activity, webRule.dynamicRender, realUrl,
            emptyMap(), object : OnWebViewResultListener {
                override fun onSuccess(html: String) {
                    listener.onSuccess(getCategory(html))
                }
                override fun onError(code: Int, msg: String) {
                    listener.onError(msg)
                }

            },
            webRule.encoding
        )

    }

    private fun getCategory(html:String):MutableList<Category>{
        val document = Jsoup.parse(html)
        val ruleParser = RuleParser(document)
        val titles = ruleParser.getStrings(webRule.categoryRule.nameRule)
        val urls = ruleParser.getStrings(webRule.categoryRule.urlRule)
        var coverUrl:List<String>? = null
        if(webRule.categoryRule.imageUrlRule != null){
            coverUrl = ruleParser.getStrings(webRule.categoryRule.imageUrlRule!!)
        }
        if(titles.isEmpty()){
            throw IllegalArgumentException("分类的名称规则填写有误，没有捕捉到任何分类名称")
        }
        if(urls.isEmpty()){
            throw IllegalArgumentException("分类的跳转url规则填写有误，没有捕捉到任何跳转url")
        }
        if(coverUrl != null && coverUrl.isEmpty()){
            throw IllegalArgumentException("分类的封面url规则填写有误，没有捕捉到任何跳转url")
        }

        if(titles.size != urls.size){
            throw IllegalStateException("捕捉的分类的名称数量和跳转url数量不一致,可能规则填写有误")
        }
        val res = mutableListOf<Category>()
        val type = if (webRule.type == RuleConstants.TYPE_VIDEO){
            Category.TYPE_VIDEO
        }else{
            Category.TYPE_PICTURE
        }
        for(i in titles.indices){

            val category = Category(titles[i],urls[i],type)
            if(coverUrl != null && i < coverUrl.size){
                category.coverUrl = coverUrl[i]
            }
            res.add(category)
        }
        return res
    }

    public fun cancel(){
        webGetter.cancel()
    }
}