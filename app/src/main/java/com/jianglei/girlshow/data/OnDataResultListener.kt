package com.jianglei.girlshow.data

/**
 * @author jianglei on 1/3/19.
 */
interface OnDataResultListener<T> {
    /**
     * 获取数据成功后回调，并携带具体数据[data]
     */
    fun onSuccess(data: T)

    /**
     * 失败时回调错误信息[msg]
     *
     */
    fun onError(msg: String)
}