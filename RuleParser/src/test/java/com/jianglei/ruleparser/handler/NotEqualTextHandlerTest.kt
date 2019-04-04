package com.jianglei.ruleparser.handler

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/4/19.
 */
class NotEqualTextHandlerTest {

    @Test
    fun getFilterStrings() {
        val strs = listOf<String>("jianglei","lei","longyi")
        var handler = NotEqualTextHandler("@!=:<jianglei,lei>")
        var res = handler.handle(null,strs)
        Assert.assertEquals(1,res.size)
        handler = NotEqualTextHandler("@!=:<jianglei>")
        res = handler.handle(null,strs)
        Assert.assertEquals(2,res.size)

        handler = NotEqualTextHandler("@!=:<jianglei,lei,longyi>")
        res = handler.handle(null,strs)
        Assert.assertEquals(0,res.size)

    }
}