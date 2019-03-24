package com.jianglei.ruleparser

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/16/19.
 */
class SelectRuleParserTest {


    @Test
    fun getClassRuleDesc(){
        var classRule = SelectRuleParser.getClassRuleDesc("@class:<name>")
        Assert.assertEquals("name",classRule.name)


        classRule = SelectRuleParser.getClassRuleDesc("@class:<name>[0]")
        Assert.assertEquals("name",classRule.name)
        Assert.assertEquals(0,classRule.index)


        classRule = SelectRuleParser.getClassRuleDesc("@class:regex<name>[0]")
        Assert.assertEquals(null,classRule.name)
        Assert.assertEquals("name",classRule.regx)
        Assert.assertEquals(0,classRule.index)
    }

    @Test
    fun getIdRuleDesc(){
        var classRule = SelectRuleParser.getIdRuleDesc("@id:<name>")
        Assert.assertEquals("name",classRule.name)


        classRule = SelectRuleParser.getIdRuleDesc("@id:<name>[0]")
        Assert.assertEquals("name",classRule.name)
        Assert.assertEquals(0,classRule.index)


        classRule = SelectRuleParser.getIdRuleDesc("@id:regex<name>[0]")
        Assert.assertEquals(null,classRule.name)
        Assert.assertEquals("name",classRule.regx)
        Assert.assertEquals(0,classRule.index)
    }


    @Test
    fun getLabelRuleDesc(){
        var classRule = SelectRuleParser.getLabelRuleDesc("@label:<p>")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals(null,classRule.index)



        classRule = SelectRuleParser.getLabelRuleDesc("@label:<p>[0]")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals(0,classRule.index)

    }


    @Test
    fun getRegexRuleDesc(){
        var classRule = SelectRuleParser.getRegexRuleDesc("@regex:<p>")
        Assert.assertEquals("p",classRule.regx)
        Assert.assertEquals(null,classRule.index)

        classRule = SelectRuleParser.getRegexRuleDesc("@regex:<<!--[\\\\s*]([\\\\d]{10})[\\\\s*]-->>[1]")
        Assert.assertEquals("<!--[\\\\s*]([\\\\d]{10})[\\\\s*]-->",classRule.regx)



        classRule = SelectRuleParser.getRegexRuleDesc("@regex:<p>[0]")
        Assert.assertEquals("p",classRule.name)
        Assert.assertEquals("p",classRule.regx)
        Assert.assertEquals(0,classRule.index)

    }


    @Test
    fun getPropertyRuleDesc(){
        val classRule = SelectRuleParser.getPropertyRuleDesc("@property:<href>")
        Assert.assertEquals("href",classRule.name)
        Assert.assertEquals(null,classRule.index)


    }
}