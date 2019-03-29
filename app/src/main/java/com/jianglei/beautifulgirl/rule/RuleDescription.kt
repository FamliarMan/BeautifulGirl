package com.jianglei.beautifulgirl.rule

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.jianglei.beautifulgirl.BR

/**
 * @author jianglei on 3/16/19.
 */
class WebRule : BaseObservable() {
    /**
     *默认为视频，可选项有 "video","image"
     */
    @get:Bindable
    var type: String = "video"
        set(value) {
            field = value
            notifyPropertyChanged(BR.type)
        }


    /**
     * 分页页面的url
     */
    @get:Bindable
    var url: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.url)
        }
    /**
     * 网站名称
     */
    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }


    @get:Bindable
    var homeUrl:String=""
    set(value){
        field = value
        notifyPropertyChanged(BR.homeUrl)
    }
    /**
     * 网站icon的url
     */
    @get:Bindable
    var icon: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.icon)
        }

    /**
     * 网站编码，默认UTF-8
     */
    @get:Bindable
    var encoding: String = "utf-8"
        set(value) {
            field = value
            notifyPropertyChanged(BR.encoding)
        }

    @get:Bindable
    var supportSearch:Boolean=false
    set(value){
        field = value
        notifyPropertyChanged(BR.supportSearch)
    }

    /**
     * 搜索规则
     */
    var searchRule: SearchRule? = null
    /**
     * 类别提取规则
     */
    var categoryRule: CategoryRule? = null

    /**
     * 某个类别下面的封面内容提取规则
     */
    var coverRule: CategoryRule? = null

    /**
     * 内容提取规则
     */
    var contentRule: CategoryRule? = null

}


/**
 * 搜索规则描述
 */
class SearchRule : BaseObservable() {

    @get:Bindable
    var dynamicRender: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.dynamicRender)
        }
    /**
     * 搜索地址,可以利用{searchTxt}占位
     */
    @get:Bindable
    var searchUrl: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.searchUrl)
        }
    /**
     * 是否支持搜索建议
     */
    @get:Bindable
    var supportSuggest: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.supportSuggest)
        }
    /**
     * 搜索建议的url地址,可以用{searchTxt}占位
     */
    @get:Bindable
    var suggestUrl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.suggestUrl)
        }
    /**
     * 搜索建议的key的提取规则
     */
    @get:Bindable
    var suggestKeyRule: String? = null
        set(value) {
            notifyPropertyChanged(BR.suggestKeyRule)
            field = value
        }
    /**
     * 某个单词数量的提取规则
     */
    @get:Bindable
    var suggestTimeRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.suggestTimeRule)
        }

    /**
     * 结果提取规则
     */
    @get:Bindable
    var resultRule: CategoryRule? = null
}


/**
 * 类别规则描述
 */
class CategoryRule : BaseObservable() {

    @get:Bindable
    var dynamicRender: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.dynamicRender)
        }
    /**
     * 名称提取规则
     */
    @get:Bindable
    var nameRule: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nameRule)
        }

    /**
     * 类别描述提取规则
     */
    @get:Bindable
    var descRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.descRule)
        }

    /**
     * 类别图片提取规则
     */
    @get:Bindable
    var imageUrlRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUrlRule)
        }

    /**
     * 类别结果url提取规则
     */
    @get:Bindable
    var urlRule: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.urlRule)
        }

    /**
     * 是否支持分页
     */
    @get:Bindable
    var supportPage: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.supportPage)
        }


    /**
     * 请求封面时真正的请求地址，某些网站虽然点击分类页面获取的
     * 地址跳转到这个页面，但页面动态性很强，真正的内容是通过另外的地址拿到的，
     * 所以这里需要单独定义一个真实地址组合规则,其中{baseUrl}代表分类中获取的地址
     */
    @get:Bindable
    var realRequestUrlRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.realRequestUrlRule)
        }


    /**
     * 是否使用webveiw加载页面，
     * 有些网站对真实地址加密，获取太过困难，可以直接使用webview加载那个网页,视屏播放页面专用
     */
    @get:Bindable
    var useWebView: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.useWebView)
        }

    /**
     * 分页规则，当[supportPage]为true时不能为空
     */
    var pageRule: PageRule? = null

}

/**
 * 分页规则
 */
class PageRule : BaseObservable() {
    /**
     * 下一个分页地址是否从上一个html页面直接获取
     * 某些网址可能会通过js计算出下一个分页的地址
     */
    @get:Bindable
    var fromHtml: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.fromHtml)
        }

    /**
     * [fromHtml] 为true时，用来抓取下一页的地址
     */
    @get:Bindable
    var nextUrlRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.nextUrlRule)
        }


    /**
     * {baseUrl}/{page}
     * url和分页参数的组合形式,其中{baseUrl}代表该页面除分页参数外的url
     * {page}代表当前分页具体参数
     *
     */
    @get:Bindable
    var combinedUrl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.combinedUrl)
        }

    /**
     * 当页码为数字页码时，设置起始页码
     */
    @get:Bindable
    var startPage: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.startPage)
        }

    /**
     * 有些网址分页不是利用数字，而是利用html页面中返回的某个值决定的
     * 这个规则用来提取那个值
     */
    @get:Bindable
    var paramRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.paramRule)
        }


}

class ContentRule : BaseObservable() {
    /**
     * 比如图片地址或视频地址的获取规则
     */
    @get:Bindable
    var detailRule: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.detailRule)
        }

    @get:Bindable
    var dynamicRender: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.dynamicRender)
        }
    /**
     * 名称的获取方式，可以为空
     */
    @get:Bindable
    var nameRule: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.nameRule)
        }
    @get:Bindable
    var supportPage: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.supportPage)
        }
    /**
     * 翻页规则
     */
    var pageRule: PageRule? = null

}

