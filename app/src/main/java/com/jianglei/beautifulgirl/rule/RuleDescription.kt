package com.jianglei.beautifulgirl.rule

/**
 * @author jianglei on 3/16/19.
 */
data class WebRule (
    /**
     *默认为视频，可选项有 "video","image"
     */
    var type:String = "video",

    /**
     * 网站名称
     */
    var name: String,

    /**
     * 网站icon的url
     */
    var icon: String?,

    /**
     * 网站编码，默认UTF-8
     */
    var encoding :String = "UTF-8",

    /**
     * 有些网站动态性太强，需要先执行js，如果需要为true
     */
    var dynamicRender:Boolean = false,

    /**
     * 搜索规则
     */
    var searchRule:SearchRule?,
    /**
     * 类别提取规则
     */
    var categoryRule:CategoryRule,

    /**
     * 某个类别下面的封面内容提取规则
     */
    var coverRule: CoverRule,

    /**
     * 内容提取规则
     */
    var contentRule:String

)

/**
 * 搜索规则描述
 */
data class SearchRule(
    /**
     * 搜索地址
     */
    var searchUrl: String,
    /**
     * 搜索关键词占位
     */
    var searchKeyHolder:String,

    /**
     * 结果名称提取规则
     */
    var resultNameRule:String,

    /**
     * 结果url提取规则
     */
    var resultImgeUrlRule:String,

    /**
     * 结果描述提取规则
     */
    var resultDescRule:String?,
    /**
     * 搜索跳转提取规则
     */
    var resultUrlRule:String


)


/**
 * 类别规则描述
 */
data class CategoryRule(
    /**
     * 分页页面的url
     */
    var url:String,
    /**
     * 名称提取规则
     */
    var nameRule:String,

    /**
     * 类别描述提取规则
     */
    var descRule:String?,

    /**
     * 类别图片提取规则
     */
    var imageUrlRule:String?,

    /**
     * 类别结果url提取规则
     */
    var urlRule:String,

    /**
     * 分页规则，为空说明不支持分页
     */
    var pageRule:PageRule?


)

/**
 * 某个类别下面内容的封面描述
 */
data class CoverRule(

    /**
     * 名称提取规则
     */
    var nameRule:String,

    /**
     * 描述提取规则
     */
    var descRule:String?,

    /**
     * 封面图片提取规则
     */
    var imageUrlRule:String?,

    /**
     * 封面结果url提取规则
     */
    var urlRule:String
)

/**
 * 分页规则
 */
data class PageRule(
    /**
     * 支持分页时分页的占位符
     */
    var pageHolder:String?,

    /**
     * 支持分页时的其实分页
     */
    var startPage:Int = 1
)

