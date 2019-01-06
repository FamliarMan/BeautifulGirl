package com.jianglei.beautifulgirl.spider

/**
 * @author jianglei on 1/2/19.
 */
interface SpiderResultListener<T> {
    /**
     * 抓取成功的返回
     */
    fun success(result:T)

    /**
     * 抓取失败
     */
    fun error(t:Throwable)
}