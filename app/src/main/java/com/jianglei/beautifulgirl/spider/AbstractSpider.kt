package com.jianglei.beautifulgirl.spider

/**
 * @author jianglei on 1/2/19.
 */
interface AbstractSpider<T>{

    /**
     *开始抓取一个网页
     */
    fun startClaw(page:Int,listener: SpiderResultListener<T>)

}