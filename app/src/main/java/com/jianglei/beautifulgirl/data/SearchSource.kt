package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.vo.SearchVideoKeyWord

/**
 * @author jianglei on 1/23/19.
 */
interface SearchSource {
    /**
     * 获取搜索建议
     */
    fun getSearchSuggest(keyword: String, listener: OnDataResultListener<MutableList<SearchVideoKeyWord>>) {}

    /**
     * 获取一个网站搜索前往的页面地址
     */
    fun getSearchUrl(searchTxt: String): String

}