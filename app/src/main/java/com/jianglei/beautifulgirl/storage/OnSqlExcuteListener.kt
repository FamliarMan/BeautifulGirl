package com.jianglei.beautifulgirl.storage

/**
 *@author longyi created on 19-3-28
 */
interface OnSqlExcuteListener<T> {
    /**
     * 子线程的操作
     */
    fun onChildThread():T

    /**
     * 主线程的回调
     */
    fun onMainThread(res:T)
}