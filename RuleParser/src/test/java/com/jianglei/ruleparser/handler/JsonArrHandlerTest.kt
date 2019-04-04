package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.GsonUtil
import com.jianglei.ruleparser.JsonRuleParser
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/4/19.
 */
class JsonArrHandlerTest {

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
        val jsonArrHandler = JsonArrHandler("@jsonArr:<users>")
        val res =  jsonArrHandler.handle(null,jsons)
        Assert.assertEquals(2, res.size)
    }


    @Test(expected = Throwable::class)
    fun getJsonArrFromString_exception() {
        val json = "hell"
        val jsons = listOf(json, json)
        val jsonArrHandler = JsonArrHandler("@jsonArr:<users>")
        val res =  jsonArrHandler.handle(null,jsons)
    }

    @Test
    fun getJsonArrFromObj() {
        val json = "{\n" +
                "  \"user1\": {\n" +
                "    \"arr\": [\n" +
                "      {\n" +
                "        \"name\": \"longyi\",\n" +
                "        \"gender\": \"male\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"longyi\",\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user2\": {\n" +
                "    \"arr\": [\n" +
                "      {\n" +
                "        \"name\": \"longyi\",\n" +
                "        \"gender\": \"male\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"longyi\",\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n"

        val element = GsonUtil.gsonParser.parse(json)
        val obj1 = element.asJsonObject.getAsJsonObject("user1")
        val obj2 = element.asJsonObject.getAsJsonObject("user2")
        val objs = listOf(obj1, obj2)

        val jsonArrHandler = JsonArrHandler("@jsonArr:<arr>")
        val res =  jsonArrHandler.handle(null,objs)
        Assert.assertEquals(2, res.size)
    }
}