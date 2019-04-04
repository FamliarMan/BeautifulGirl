package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.GsonUtil

import org.junit.Assert
import org.junit.Test
/**
 * @author jianglei on 4/4/19.
 */
class JsonValueHandlerTest {

    @Test
    fun getJsonValueFromString() {
        val json = "{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      }"
        val handler = JsonValueHandler("@jsonValue:<last>")
        val res = handler.handle(null, listOf(json,json))
        Assert.assertEquals(
            "jiang",
            res[0]
        )
    }

    @Test
    fun getJsonValueFromObj() {
        val json = "{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      }"
        val obj = GsonUtil.gsonParser.parse(json).asJsonObject
        val handler = JsonValueHandler("@jsonValue:<last>")
        val res = handler.handle(null, listOf(obj,obj))
        Assert.assertEquals(
            "jiang",
            res[1]
        )
    }

    @Test
    fun getJsonValueFromArr() {
        val json = "{\n" +
                "  \"arr\": [\n" +
                "    {\n" +
                "      \"name\":{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      },\n" +
                "      \"gender\": \"male\"\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "      \"name\":{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      },\n" +
                "      \"gender\": \"male\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n"
        val root = GsonUtil.gsonParser.parse(json)
        val arr = root.asJsonObject.getAsJsonArray("arr")
        val handler = JsonValueHandler("@jsonValue:<gender>")
        val res = handler.handle(null, listOf(arr,arr))
        Assert.assertEquals(4,res.size)
        Assert.assertEquals("male",res[3])

    }
}