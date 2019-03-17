package com.jianglei.ruleparser

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/16/19.
 */
class SingleRuleParserTest {


    @Test
    fun getClassRuleDesc(){
        var classRule = SingleRuleParser.getClassRuleDesc("@class:<name>")
        Assert.assertEquals("name",classRule.name)


        classRule = SingleRuleParser.getClassRuleDesc("@class:<name>[0]")
        Assert.assertEquals("name",classRule.name)
        Assert.assertEquals(0,classRule.index)


        classRule = SingleRuleParser.getClassRuleDesc("@class:regx<name>[0]")
        Assert.assertEquals(null,classRule.name)
        Assert.assertEquals("name",classRule.regx)
        Assert.assertEquals(0,classRule.index)
    }

    @Test
    fun getIdRuleDesc(){
        var classRule = SingleRuleParser.getIdRuleDesc("@id:<name>")
        Assert.assertEquals("name",classRule.name)


        classRule = SingleRuleParser.getIdRuleDesc("@id:<name>[0]")
        Assert.assertEquals("name",classRule.name)
        Assert.assertEquals(0,classRule.index)


        classRule = SingleRuleParser.getIdRuleDesc("@id:regx<name>[0]")
        Assert.assertEquals(null,classRule.name)
        Assert.assertEquals("name",classRule.regx)
        Assert.assertEquals(0,classRule.index)
    }


    @Test
    fun getLabelRuleDesc(){
        var classRule = SingleRuleParser.getLabelRuleDesc("@label:<p>")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals(null,classRule.index)



        classRule = SingleRuleParser.getLabelRuleDesc("@label:<p>[0]")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals(0,classRule.index)

    }


    @Test
    fun getRegexRuleDesc(){
        var classRule = SingleRuleParser.getRegexRuleDesc("@regex:<p>")
        Assert.assertEquals("p",classRule.regx)
        Assert.assertEquals(null,classRule.index)



        classRule = SingleRuleParser.getRegexRuleDesc("@regex:<p>[0]")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals(0,classRule.index)

    }


    @Test
    fun getPropertyRuleDesc(){
        val classRule = SingleRuleParser.getPropertyRuleDesc("@property:<href>")
        Assert.assertEquals("href",classRule.name)
        Assert.assertEquals(null,classRule.index)


    }
}