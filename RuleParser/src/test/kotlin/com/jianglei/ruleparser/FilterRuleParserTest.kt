package com.jianglei.ruleparser

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/19/19.
 */
class FilterRuleParserTest {
    @Test
    fun getConditionValue(){
        Assert.assertEquals(null,
            FilterRuleParser.getConditionValue("@hasClass<>"))
        Assert.assertEquals(null,
            FilterRuleParser.getConditionValue("@Class<>"))
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@hasClass<class1,class2>")!![1])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noClass<class1,class2>")!![1])
        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@hasId<class1,class2>")!![0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noId<class1,class2>")!![1])

        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@hasLabel<class1,class2>")!![0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noLabel<class1,class2>")!![1])

        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@==<class1,class2>")!![0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@!=<class1,class2>")!![1])
    }
}