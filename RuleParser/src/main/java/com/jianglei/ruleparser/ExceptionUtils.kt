package com.jianglei.ruleparser

/**
 * @author jianglei on 3/20/19.
 */
class ExceptionUtils {
    companion object {
        /**
         * 由于一些不知名的原因，ide检测到IllegalArgumentException会报错，
         * 这里集中起来调用，避免ide报错
         */
        fun throwIllegalArgumentException(msg: String):Nothing {
            throw IllegalArgumentException(msg)
        }
    }
}