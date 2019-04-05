package com.jianglei.girlshow.storage

/**
 *@author longyi created on 19-3-28
 */
interface OnAsyncUtilListener<T> {
    /**
     * 子线程的操作
     */
    fun onChildThread():T

    /**
     * 主线程的回调
     */
    fun onMainThread(res:T)
}