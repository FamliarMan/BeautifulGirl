package com.jianglei.ruleparser

import android.os.Handler
import android.os.Looper
import com.jianglei.ruleparser.handler.AbstractRuleHandler
import com.jianglei.ruleparser.handler.HandlerFactroy
import com.jianglei.ruleparser.handler.IllegalSyntaxException
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

/**
 * @author jianglei on 3/16/19.
 */
class HtmlParser() {
    private val elementsCache: MutableMap<String, Elements> = mutableMapOf()
    private val jsonCache: MutableMap<String, List<Any>> = mutableMapOf()
    private val nodeCache: MutableMap<String, List<Any>> = mutableMapOf()
    private var jobs: MutableList<FutureTask<List<List<String>>>> = mutableListOf()

    companion object {
        public val mExecutor = Executors.newFixedThreadPool(5)
        val mHandler = Handler(Looper.getMainLooper())
    }

    fun reset() {
        nodeCache.clear()
        jobs.clear()
    }

    fun getStringsUnitAsnyc(
        vararg rules: String?,
        document: Document,
        onParserSuccessListener: OnParserSuccessListener
    ) {
        reset()
        val parserJob = FutureTask<List<List<String>>>(Callable<List<List<String>>> {
            val res = mutableListOf<List<String>>()

            try {

                rules.forEach {
                    if (it.isNullOrBlank()) {
                        res.add(emptyList())
                    } else {
                        res.add(getStringsUnit(it, document))
                    }
                }
                mHandler.post {
                    onParserSuccessListener.parserSuccess(res)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mHandler.post {
                    onParserSuccessListener.parserError(e.toString())
                }
            }
            res
        })
        jobs = jobs.filter {
            !it.isDone
        }.toMutableList()
        jobs.add(parserJob)
        mExecutor.execute(parserJob)

    }

    fun cancel() {
        jobs.forEach {
            it.cancel(false)
        }
    }

    /**
     * 处理规则 or 的情况
     */
    fun getStringsUnit(rule: String, document: Document): List<String> {
        val mainRules = rule.split(" or ")
        var res = mutableListOf<String>()
        for(i in 0 until mainRules.size){
            var oneRes = getStrings(mainRules[i], document)
            if(i == mainRules.size-1){
                //最后一条规则，无论如何都塞进结果
                res.addAll(oneRes)

            }else{
                //不是最后一条,且当前没有收集到任何数据，我们继续查使用下面一条规则解析
                if (res.isEmpty()) {
                    oneRes = oneRes.filter {
                        !it.isBlank()
                    }.toList()
                    if(!oneRes.isEmpty()){
                        res.addAll(oneRes)
                        //找到有用数据，无需再继续往后查找了
                        break
                    }
                }
            }
        }
        return res
    }

    /**
     * 根据规则获取字符串结果，返回值统一为列表，便于抽象
     */
    fun getStrings(rule: String, document: Document): List<String> {
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
                LogUtil.d("规则${totalRule}解析结果数量：${preResult.size}")
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

    interface OnParserSuccessListener {
        fun parserSuccess(res: List<List<String>>)
        fun parserError(msg: String)
    }
}