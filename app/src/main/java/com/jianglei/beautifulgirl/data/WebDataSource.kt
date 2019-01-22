package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.vo.*
import java.io.Serializable

/**
 * 一个具体的数据来源，比如爬虫，比如接口
 * @author jianglei on 1/3/19.
 */
interface WebDataSource:Serializable{

    /**
     * 获取这个网站的描述
     */
    fun fetchWebsite():WebsiteDescVo
    /**
     * 获取第几页的数据，[page]为页码，从1开始，需要传入监听[OnDataResultListener],
     * 以便获取数据
     */
     fun  fetchCoverContents(url:String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>)

    /**
     * 获取某个帖子的所有图片数据
     */
    fun fetDetailPictures(url:String,page:Int,listener: OnDataResultListener<MutableList<String>>){}

    /**
     * 获取某个网站的所有分类栏目
     */
    fun fetchAllCategory(homePageUrl:String, listener: OnDataResultListener<MutableList<Category>>, page:Int=1)

    /**
     * 获取视频地址
     */
    fun fetchVideoUrls(detailUrl:String, listener: OnDataResultListener<MutableList<PlayUrl>>){
    }
    /**
     * 取消所有请求
     */
    fun cancelAllNet(){
        RetrofitManager.cancelNet()
    }


    /**
     * 获取搜索建议
     */
    fun getSearchSuggest(keyword:String,listener:OnDataResultListener<MutableList<SearchVideoKeyWord>>){}

    /**
     * 获取一个网站搜索前往的页面地址
     */
    fun getSearchUrl(searchTxt:String):String{
        return ""
    }

}