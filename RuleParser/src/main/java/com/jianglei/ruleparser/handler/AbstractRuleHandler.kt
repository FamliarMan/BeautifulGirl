package com.jianglei.ruleparser.handler

import com.google.gson.JsonObject
import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.json.JSONArray
import org.w3c.dom.Element
import kotlin.math.sin
import kotlin.reflect.KClass

/**
 * @author jianglei on 3/30/19.
 */
abstract class AbstractRuleHandler(var singleRule:String) {
    companion object {
        const val TYPE_HTML_ELEMENT = 1
        const val TYPE_STRING = 2
        const val TYPE_JSON_OBJ = 3
        const val TYPE_JSON_ARR = 4
        val typeClass = mapOf(
            TYPE_HTML_ELEMENT to Element::class,
            TYPE_STRING to String::class,
            TYPE_JSON_OBJ to JsonObject::class,
            TYPE_JSON_ARR to JSONArray::class
        )
    }

    /**
     * 检查本规则能否处理上个规则返回的结果，如果可以返回要处理的数据类型
     */
    fun checkAndReturnClassTyepe(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): KClass<out Any> {
        if(lastRuleHandler == null){
            return
        }
        acceptType().forEach {
            if (it == lastRuleHandler.targetType()) {
                val type = typeClass[it] ?: throw IllegalStateException("没有注册该类型对应的KClass")
                preResult.forEach { res ->
                    if (res::class == type) {
                        return type
                    } else {
                        throw IllegalStateException("上一个规则:$singleRule 返回的结果类型不对")
                    }
                }
            }
        }
        throw IllegalSyntaxException(singleRule + "不能跟在" + lastRuleHandler.singleRule + "后面")
    }

    /**
     * 接受的类型,可能是多种类型
     */
    abstract fun acceptType(): List<Int>

    /**
     * 处理的结果的类型
     */
    abstract fun targetType(): Int

    /**
     * 将上一个规则处理后的结果处理后输出
     */
    abstract fun handle(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): List<Any>

    /**
     * 获取规则
     */
    abstract fun getRuleDesc(): RuleDesc

    fun getRuleDesc(
        rule: String, nameRegexPattern: Regex?,
        namePattern: Regex
    ): RuleDesc {

        var m = nameRegexPattern?.find(rule)
        if (m != null) {
            val nameRegx = m.groupValues[1]
            val index: Int? = if (m.groupValues[3] == "") {
                null
            } else {
                m.groupValues[3].toInt()
            }
            return RuleDesc(null, nameRegx, index)
        }
        m = namePattern.find(rule)
        if (m != null) {
            //使用类名完全匹配类名

            val name = m.groupValues[1]
            val index: Int? = if (m.groupValues.size < 4 || m.groupValues[3] == "") {
                null
            } else {
                m.groupValues[3].toInt()
            }
            return RuleDesc(name, null, index)
        }

        throw IllegalArgumentException("不合法的规则描述:$rule")
    }

}