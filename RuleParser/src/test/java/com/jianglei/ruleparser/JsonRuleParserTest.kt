package com.jianglei.ruleparser

import com.google.gson.Gson
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
        Assert.assertEquals(2, res.size)
    }


    @Test(expected = Throwable::class)
    fun getJsonArrFromString_exception() {
        val json = "hell"
        val jsons = listOf(json, json)
        var res = JsonRuleParser.getJsonArrFromString(
            "@jsonArr:<users>",
            jsons
        )
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
        val res = JsonRuleParser.getJsonArrFromObj("@jsonArr:<arr>", objs)
        Assert.assertEquals(2, res.size)
    }


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
        val res = JsonRuleParser.getJsonObjFromString(
            "@jsonObj:<user1>",
            jsons
        )
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
        val res = JsonRuleParser.getJsonObjFromObj(
            "@jsonObj:<arr>",
            objs
        )
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
        val res = JsonRuleParser.getJsonObjFromArr("@jsonObj:<name>", arrs)
        Assert.assertEquals(4, res.size)
    }

    @Test
    fun getJsonValueFromString() {
        val json = "{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      }"
        Assert.assertEquals(
            "jiang",
            JsonRuleParser.getJsonValueFromString("@jsonValue:<last>", listOf(json, json))[1]
        )
    }

    @Test
    fun getJsonValueFromObj() {
        val json = "{\n" +
                "        \"last\":\"jiang\",\n" +
                "        \"first\":\"lei\"\n" +
                "      }"
        val obj = GsonUtil.gsonParser.parse(json).asJsonObject
        Assert.assertEquals(
            "jiang",
            JsonRuleParser.getJsonValueFromObj(
                "@jsonValue:<last>",
                listOf(obj, obj)
            )[1]
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
        val res = JsonRuleParser.getJsonValueFromArr(
            "@jsonValue:<gender>", listOf(arr, arr)
        )
        Assert.assertEquals(4,res.size)
        Assert.assertEquals("male",res[3])

    }

}