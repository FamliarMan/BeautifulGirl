package com.jianglei.ruleparser.ruledescription

/**
 * @author jianglei on 3/16/19.
 */

data class RuleDesc(
    /**
     * 名称，比如类名，属性名，id名,为空说明使用正则形式
     */
    val name:String?,
    /**
     * 名称的正则，如果为空，不使用正则
     */
    val regx:String?,
    /**
     * 结果的index，比如0说明取列表结果的第一个元素，
     */
    val index:Int?
)

data class FilterRuleDesc(
    /**
     * 过滤的值
     */
    val values:List<String>
)