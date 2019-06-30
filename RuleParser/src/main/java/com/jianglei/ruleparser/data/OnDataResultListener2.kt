package com.jianglei.ruleparser.data

/**
 * @author jianglei on 1/3/19.
 */
interface OnDataResultListener2<T1,T2> {
    /**
     * 获取数据成功后回调，并携带具体数据[data]
     */
    fun onSuccess(data1: T1?,data2:T2?)

    /**
     * 失败时回调错误信息[msg]
     *
     */
    fun onError(msg: String)
}