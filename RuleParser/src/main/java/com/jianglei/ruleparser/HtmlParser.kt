package com.jianglei.ruleparser

import com.jianglei.ruleparser.handler.AbstractRuleHandler
import com.jianglei.ruleparser.handler.HandlerFactroy
import com.jianglei.ruleparser.handler.IllegalSyntaxException
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * @author jianglei on 3/16/19.
 */
class HtmlParser(private val document: Document) {
    private val elementsCache: MutableMap<String, Elements> = mutableMapOf()
    private val jsonCache: MutableMap<String, List<Any>> = mutableMapOf()
    private val nodeCache: MutableMap<String, List<Any>> = mutableMapOf()

    /**
     * 处理规则 or 的情况
     */
    fun getStringsUnit(rule: String): List<String> {
        val mainRules = rule.split(" or ")
        var res = mutableListOf<String>()
        mainRules.forEach {
            if (res.isEmpty()) {
                val oneRes = getStrings(it)
                res.addAll(oneRes)
            }

        }
        return res
    }

    /**
     * 根据规则获取字符串结果，返回值统一为列表，便于抽象
     */
    fun getStrings(rule: String): List<String> {
        val singleRules = rule.split("->")
        //上一个规则得到的元素结果
        var preElements = Elements()
        var totalRule = ""
        var preResult = mutableListOf<Any>()
        var preHandler: AbstractRuleHandler? = null
        preResult.add(document)
        try {

            for (singleRule in singleRules) {
                totalRule = if (totalRule.isEmpty()) {
                    totalRule.plus(singleRule.trim())
                } else {
                    totalRule.plus("->").plus(singleRule.trim())
                }

                val curHandler = HandlerFactroy.create(singleRule)
                if (nodeCache.containsKey(totalRule)) {
                    preResult = nodeCache[totalRule] as MutableList<Any>
                } else {
                    preResult = curHandler.handle(preHandler, preResult) as MutableList<Any>
                    nodeCache[totalRule] = preResult
                }
                preHandler = curHandler
            }
            if (!preHandler!!.isStringRule()) {
                throw  IllegalSyntaxException("该规则${preHandler.singleRule}无法获得字符串结果")
            }
            @Suppress("UNCHECKED_CAST")
            return preResult as List<String>


        } catch (e: Throwable) {
            e.printStackTrace()
            if (e is IllegalArgumentException) {
                ExceptionUtils.throwIllegalArgumentException(e.toString())
            } else {
                ExceptionUtils.throwIllegalArgumentException("非法规则:$rule")
            }
        }

    }

}