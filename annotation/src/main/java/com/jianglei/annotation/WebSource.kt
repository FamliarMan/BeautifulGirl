package com.jianglei.annotation

/**
 * @author jianglei on 1/19/19.
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSource(val needVpn :Boolean,val index:Int=0)