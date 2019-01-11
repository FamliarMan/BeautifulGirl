package com.jianglei.beautifulgirl.data

/**
 * @author jianglei on 1/11/19.
 */
interface OnItemClickListener<T> {
    fun onItemClick(item:T,pos:Int);
}