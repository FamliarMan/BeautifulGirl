package com.jianglei.ruleparser

import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/20/19.
 */

class JsonRuleParserTest {
    @Test
    fun getKeyName() {
        Assert.assertEquals("name", JsonRuleParser.getKeyName("@jsonArr:<name>"))
        Assert.assertEquals("name", JsonRuleParser.getKeyName("@jsonValue:<name>"))
        Assert.assertEquals("name", JsonRuleParser.getKeyName("@jsonObj:<name>"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getKeyName_exception() {
        Assert.assertEquals("name", JsonRuleParser.getKeyName("@jsonArr:<>"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getKeyName_exception2() {
        Assert.assertEquals("name", JsonRuleParser.getKeyName("@nArr:<>"))
    }

    @Test
    fun getJsonArrFromString() {
        val json = "{\n" +
                "  \"users\": [\n" +
                "    {\n" +
                "      \"name\": \"longyi\",\n" +
                "      \"gender\": \"male\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"jianglei\",\n" +
                "      \"gender\": \"male\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n"
        val jsons = listOf(json, json)
        var res = JsonRuleParser.getJsonArrFromString(
            "@jsonArr:<users>",
            jsons
        )
        Assert.assertEquals(2,res.size)
    }


    @Test(expected =Throwable::class)
    fun getJsonArrFromString_exception() {
        val json = "hell"
        val jsons = listOf(json, json)
        var res = JsonRuleParser.getJsonArrFromString(
            "@jsonArr:<users>",
            jsons
        )
    }
}