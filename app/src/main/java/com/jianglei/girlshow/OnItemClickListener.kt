package com.jianglei.girlshow

/**
 * @author jianglei on 1/6/19.
 */
interface OnItemClickListener <T>{

    fun onItemClick(vo:T,pos:Int)
}