package com.jianglei.ruleparser.handler

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/4/19.
 */
class EqualTextHandlerTest {

    @Test
    fun getFilterStrings() {
        val strs = listOf<String>("jianglei","lei","longyi")
        var handler = EqualTextHandler("@==:<jianglei>")
        var res = handler.handle(null,strs)
        Assert.assertEquals(1,res.size)
        handler = EqualTextHandler("@==:<jianglei>")
        res = handler.handle(null,strs)
        Assert.assertEquals("jianglei",res[0])

        handler = EqualTextHandler("@==:<jianglei,lei>")
        res = handler.handle(null,strs)
        Assert.assertEquals(2,res.size)

    }
}