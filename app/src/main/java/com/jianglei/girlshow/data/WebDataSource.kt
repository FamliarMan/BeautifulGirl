package com.jianglei.girlshow.data

import androidx.fragment.app.FragmentActivity
import com.jianglei.girlshow.vo.Category
import com.jianglei.girlshow.vo.ContentTitle
import com.jianglei.girlshow.vo.WebsiteDescVo
import java.io.Serializable

/**
 * 一个具体的数据来源，比如爬虫，比如接口
 * @author jianglei on 1/3/19.
 */
interface WebDataSource : Serializable {

    val id: String
        get() = this.javaClass.name

    /**
     * 获取这个网站的描述
     */
    fun fetchWebsite(): WebsiteDescVo

    /**
     * 获取第几页的数据，[page]为页码，从1开始，需要传入监听[OnDataResultListener],
     * 以便获取数据
     */
    fun fetchCoverContents(activity:FragmentActivity,url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>)


    /**
     * 获取某个网站的所有分类栏目
     */
    fun fetchAllCategory(homePageUrl: String, listener: OnDataResultListener<MutableList<Category>>, page: Int = 1)


    /**
     * 取消所有请求
     */
    fun cancelAllNet() {
        RetrofitManager.cancelNet()
    }



}