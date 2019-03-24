package com.jianglei.beautifulgirl.rule

/**
 * @author jianglei on 3/16/19.
 */
data class WebRule(
    /**
     *默认为视频，可选项有 "video","image"
     */
    var type: String = "video",

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
    var encoding: String = "UTF-8",

    /**
     * 有些网站动态性太强，需要先执行js，如果需要为true
     */
    var dynamicRender: Boolean = false,

    /**
     * 搜索规则
     */
    var searchRule: SearchRule?,
    /**
     * 类别提取规则
     */
    var categoryRule: CategoryRule,

    /**
     * 某个类别下面的封面内容提取规则
     */
    var coverRule: CategoryRule,

    /**
     * 内容提取规则
     */
    var contentRule: ContentRule

)

/**
 * 搜索规则描述
 */
data class SearchRule(
    /**
     * 搜索地址,可以利用{searchTxt}占位
     */
    var searchUrl: String,
    /**
     * 结果提取规则
     */
    var resultRule:CategoryRule,

    /**
     * 搜索建议的url地址,可以用{searchTxt}占位
     */
    var suggestUrl:String?,
    /**
     * 搜索建议的key的提取规则
     */
    var suggestKeyRule:String?,
    /**
     * 某个单词数量的提取规则
     */
    var suggestTimeRule:String?

)


/**
 * 类别规则描述
 */
data class CategoryRule(

    var dynamicRender: Boolean,
    /**
     * 分页页面的url
     */
    var url: String,
    /**
     * 名称提取规则
     */
    var nameRule: String,

    /**
     * 类别描述提取规则
     */
    var descRule: String?,

    /**
     * 类别图片提取规则
     */
    var imageUrlRule: String?,

    /**
     * 类别结果url提取规则
     */
    var urlRule: String,

    /**
     * 分页规则，为空说明不支持分页
     */
    var pageRule: PageRule?,

    /**
     * 请求封面时真正的请求地址，某些网站虽然点击分类页面获取的
     * 地址跳转到这个页面，但页面动态性很强，真正的内容是通过另外的地址拿到的，
     * 所以这里需要单独定义一个真实地址组合规则,其中{baseUrl}代表分类中获取的地址
     */
    var realRequestUrlRule: String?


)

/**
 * 分页规则
 */
data class PageRule(
    /**
     * 下一个分页地址是否从上一个html页面直接获取
     * 某些网址可能会通过js计算出下一个分页的地址
     */
    var isFromHtml: Boolean = true,

    /**
     * [isFromHtml] 为true时，用来抓取下一页的地址
     */
    var nextUrlRule: String?,


    /**
     * {baseUrl}/{page}
     * url和分页参数的组合形式,其中{baseUrl}代表该页面除分页参数外的url
     * {page}代表当前分页具体参数
     *
     */
    var combinedUrl: String?,

    /**
     * 当页码为数字页码时，设置起始页码
     */
    var startPage: Int?,

    /**
     * 有些网址分页不是利用数字，而是利用html页面中返回的某个值决定的
     * 这个规则用来提取那个值
     */
    var paramRule: String?


)

data class ContentRule(
    /**
     * 比如图片地址或视频地址的获取规则
     */
    var detailRule: String,

    var dynamicRender: Boolean,
    /**
     * 名称的获取方式，可以为空
     */
    var nameRule: String?,
    /**
     * 翻页规则
     */
    var pageRule: PageRule?,
    /**
     * 是否使用webveiw加载页面，
     * 有些网站对真实地址加密，获取太过困难，可以直接使用webview加载那个网页
     */
    var useWebView: Boolean = false,

    /**
     * 请求封面时真正的请求地址，某些网站虽然点击分类页面获取的
     * 地址跳转到这个页面，但页面动态性很强，真正的内容是通过另外的地址拿到的，
     * 所以这里需要单独定义一个真实地址组合规则,其中{baseUrl}代表分类中获取的地址
     */
    var realRequestUrlRule: String?
)