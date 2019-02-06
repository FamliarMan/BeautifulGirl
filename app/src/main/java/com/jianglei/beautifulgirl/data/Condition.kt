package com.jianglei.beautifulgirl.data

/**
 * @author jianglei on 2/6/19.
 */
interface Condition<T> {
    fun isValid(t : T):Boolean

}