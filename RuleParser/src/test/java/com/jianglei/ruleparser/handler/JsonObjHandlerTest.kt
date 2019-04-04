package com.jianglei.ruleparser.handler

import com.jianglei.ruleparser.GsonUtil
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/4/19.
 */
class JsonObjHandlerTest {
    @Test
    fun getJsonObjFromString() {
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
        val jsons = listOf(json, json)
        val handler = JsonObjHandler("@jsonObj:<user1>")
         val res = handler.handle(null,jsons)
        Assert.assertEquals(2, res.size)
    }


    @Test
    fun getJsonObjFromObj() {
        val json = "{\n" +
                "  \"user1\": {\n" +
                "    \"arr\": {\n" +
                "      \"name\": \"longyi\",\n" +
                "      \"gender\": \"male\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"user2\": {\n" +
                "    \"arr\": {\n" +
                "      \"name\": \"longyi\",\n" +
                "      \"gender\": \"male\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n"
        val obj = GsonUtil.gsonParser.parse(json).asJsonObject.getAsJsonObject("user1")
        val objs = listOf(obj, obj)
        val handler = JsonObjHandler("@jsonObj:<arr>")
        val res = handler.handle(null,objs)
        Assert.assertEquals(2, res.size)
    }

    @Test
    fun getJsonObjFromArr() {
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
        val root = GsonUtil.gsonParser.parse(json).asJsonObject
        val arr = root.getAsJsonArray("arr")
        val arrs = listOf(arr, arr)
        val handler = JsonObjHandler("@jsonObj:<name>")
        val res = handler.handle(null,arrs)
        Assert.assertEquals(4, res.size)
    }

}