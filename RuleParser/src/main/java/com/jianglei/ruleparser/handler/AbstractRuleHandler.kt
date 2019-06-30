package com.jianglei.ruleparser.handler

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jianglei.ruleparser.RuleKeyWord
import com.jianglei.ruleparser.ruledescription.RuleDesc
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import kotlin.reflect.KClass

/**
 * @author jianglei on 3/30/19.
 */
abstract class AbstractRuleHandler(var singleRule: String) {
    companion object {
        const val TYPE_HTML_ELEMENT = 1
        const val TYPE_STRING = 2
        const val TYPE_JSON_OBJ = 3
        const val TYPE_JSON_ARR = 4
        val typeClass = mapOf(
            TYPE_HTML_ELEMENT to Element::class,
            TYPE_STRING to String::class,
            TYPE_JSON_OBJ to JsonObject::class,
            TYPE_JSON_ARR to JsonArray::class
        )

        /**
         * 判断[selectRule]是否是一个元素提取规则
         */
        fun isElementSelectRule(selectRule: String): Boolean {
            val rule = selectRule.trim()
            return rule.startsWith(RuleKeyWord.CLASS)
                    || rule.startsWith(RuleKeyWord.ID)
                    || rule.startsWith(RuleKeyWord.LABEL)
        }

        /**
         * 判断[selectRule] 是否是一个字符串提取规则
         */
        fun isStringSelectRule(selectRule: String): Boolean {
            val rule = selectRule.trim()
            return rule.startsWith(RuleKeyWord.PROPERTY)
                    || rule.startsWith(RuleKeyWord.TEXT)
                    || rule.startsWith(RuleKeyWord.REGX)
        }

        /**
         * 判断当前规则是否是用来过滤html元素的
         */
        fun isElementFilterRule(filterRule: String): Boolean {
            val rule = filterRule.trim()

            return rule.startsWith(RuleKeyWord.HAS_CLASS)
                    || rule.startsWith(RuleKeyWord.HAS_ID)
                    || rule.startsWith(RuleKeyWord.HAS_LABEL)
                    || rule.startsWith(RuleKeyWord.NO_LABEL)
                    || rule.startsWith(RuleKeyWord.NO_CLASS)
                    || rule.startsWith(RuleKeyWord.NO_ID)
                    || rule.startsWith(RuleKeyWord.HAS_TEXT)
                    || rule.startsWith(RuleKeyWord.NO_TEXT)
        }

        /**
         * 判断当前规则是否用来过滤字符串
         */
        fun isStringFilterRule(filterRule: String): Boolean {
            val rule = filterRule.trim()

            return rule.startsWith(RuleKeyWord.EQUALS)
                    || rule.startsWith(RuleKeyWord.NOT_EQUAL)
        }
    }

    /**
     * 检查本规则能否处理上个规则返回的结果，如果可以返回要处理的数据类型
     */
    fun checkAndReturnClassTyepe(lastRuleHandler: AbstractRuleHandler?, preResult: List<Any>): KClass<out Any> {
        if (lastRuleHandler == null) {
            //如果没有上一个规则，只可能是第一个规则，返回的必然是
            if (acceptType().size == 1) {
                //只接收一个类型
                return typeClass.getValue(acceptType()[0])
            } else {
                return preResult[0]::class
            }
        }
        acceptType().forEach {
            if (it == lastRuleHandler.targetType()) {
                val type = typeClass[it] ?: throw IllegalStateException("没有注册该类型对应的KClass")
                //检查上一个结果是否符合本规则输入类型
                preResult.forEach { res ->
//                    if(!type::class.java.isAssignableFrom(res::class.java)){
                    if (res::class != type) {
                        throw IllegalStateException("上一个规则返回的类型${res::class.toString()}本规则：${singleRule}无法处理")
                    }
                }
                return type
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


    /**
     * 判断当前规则返回的是否是字符串
     */
    fun isStringRule(): Boolean {
        val rule = singleRule.trim()
        return rule.startsWith(RuleKeyWord.PROPERTY)
                || rule.startsWith(RuleKeyWord.TEXT)
                || rule.startsWith(RuleKeyWord.REGX)
                || rule.startsWith(RuleKeyWord.JSON_VALUE)
                || rule.startsWith(RuleKeyWord.EQUALS)
                || rule.startsWith(RuleKeyWord.NOT_EQUAL)
    }
}