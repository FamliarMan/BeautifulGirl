package com.jianglei.ruleparser

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test
import org.junit.runner.manipulation.Filter

/**
 * @author jianglei on 3/19/19.
 */
class FilterRuleParserTest {
    @Test
    fun getConditionValue(){
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noClass:<class1,class2>")[1])
        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@hasId:<class1,class2>")[0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noId:<class1,class2>")[1])

        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@hasLabel:<class1,class2>")[0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@noLabel:<class1,class2>")[1])

        Assert.assertEquals("class1",
            FilterRuleParser.getConditionValue("@==:<class1,class2>")[0])
        Assert.assertEquals("class2",
            FilterRuleParser.getConditionValue("@!=:<class1,class2>")[1])
    }


    @Test
    fun isElementFilterRule(){
        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@hasClass"))

        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@noClass"))
        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@hasId"))
        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@noId"))
        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@hasLabel"))
        Assert.assertEquals(true,
            FilterRuleParser.isElementFilterRule("@noLabel"))
        Assert.assertEquals(false,
            FilterRuleParser.isElementFilterRule("@=="))
        Assert.assertEquals(false,
            FilterRuleParser.isElementFilterRule("@!="))
    }

    @Test
    fun isStringFilterRule(){
        Assert.assertEquals(false,
            FilterRuleParser.isStringFilterRule("@noLabel"))
        Assert.assertEquals(true,
            FilterRuleParser.isStringFilterRule("@=="))
        Assert.assertEquals(true,
            FilterRuleParser.isStringFilterRule("@!="))
    }


    /**
     * 测试@hasClass,@noClass
     */
    @Test
    fun getFilterElements_class(){
        val html = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body class=\"face-search\">\n" +
                "\n" +
                "<div class=\"tradeindex_wrapper\">\n" +
                "    <h2>More free porn</h2>\n" +
                "    <div class=\"freemore\">\n" +
                "        <ol start=\"1\" class=\"globaltop col1\">\n" +
                "            <li><a href=\"//hotmovs.com/\" target=\"_blank\">Hot Movs</a></li>\n" +
                "            <li><a href=\"//www.txxx.com/\" target=\"_blank\">Txxx</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"
        val preElements = Jsoup.parse(html).getElementsByTag("body")
        var res = FilterRuleParser.getFilterElements(preElements,"@hasClass:<freemore>")
        Assert.assertEquals(1,res.size)
        res = FilterRuleParser.getFilterElements(preElements,"@hasClass:<emore>")
        Assert.assertEquals(0,res.size)


        res = FilterRuleParser.getFilterElements(preElements,"@noClass:<emore>")
        Assert.assertEquals(1,res.size)

        res = FilterRuleParser.getFilterElements(preElements,"@noClass:<freemore>")
        Assert.assertEquals(0,res.size)
    }

    @Test
    fun getFilterElements_id() {
        val html = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body class=\"face-search\">\n" +
                "\n" +
                "<div class=\"tradeindex_wrapper\">\n" +
                "    <h2>More free porn</h2>\n" +
                "    <div class=\"freemore\">\n" +
                "        <ol start=\"1\" class=\"globaltop col1\">\n" +
                "            <li><a href=\"//hotmovs.com/\" target=\"_blank\">Hot Movs</a></li>\n" +
                "            <li><a href=\"//www.txxx.com/\" target=\"_blank\">Txxx</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "        <div id=\"name\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"
        val preElements = Jsoup.parse(html).getElementsByTag("body")
        var res = FilterRuleParser.getFilterElements(preElements, "@hasId:<name>")
        Assert.assertEquals(1,res.size)

        res = FilterRuleParser.getFilterElements(preElements, "@noId:<name>")
        Assert.assertEquals(0,res.size)
    }

    @Test
    fun getFilterElements_label() {
        val html = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body class=\"face-search\">\n" +
                "\n" +
                "<div class=\"tradeindex_wrapper\">\n" +
                "    <h2>More free porn</h2>\n" +
                "    <div class=\"freemore\">\n" +
                "        <ol start=\"1\" class=\"globaltop col1\">\n" +
                "            <li><a href=\"//hotmovs.com/\" target=\"_blank\">Hot Movs</a></li>\n" +
                "            <li><a href=\"//www.txxx.com/\" target=\"_blank\">Txxx</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "        <div id=\"name\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"
        val preElements = Jsoup.parse(html).getElementsByTag("body")
        var res = FilterRuleParser.getFilterElements(preElements, "@hasLabel:<li>")
        Assert.assertEquals(1,res.size)

        res = FilterRuleParser.getFilterElements(preElements, "@noLabel:<li>")
        Assert.assertEquals(0,res.size)
    }


    @Test(expected = IllegalArgumentException::class)
    fun getFilterElements_exception() {
        val html = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body class=\"face-search\">\n" +
                "\n" +
                "<div class=\"tradeindex_wrapper\">\n" +
                "    <h2>More free porn</h2>\n" +
                "    <div class=\"freemore\">\n" +
                "        <ol start=\"1\" class=\"globaltop col1\">\n" +
                "            <li><a href=\"//hotmovs.com/\" target=\"_blank\">Hot Movs</a></li>\n" +
                "            <li><a href=\"//www.txxx.com/\" target=\"_blank\">Txxx</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "        <div id=\"name\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"
        val preElements = Jsoup.parse(html).getElementsByTag("body")
        var res = FilterRuleParser.getFilterElements(preElements, "@Label:<li>")
        Assert.assertEquals(1,res.size)

    }


    @Test
    fun getFilterStrings() {
        val strs = listOf<String>("jianglei","lei","longyi")
        Assert.assertEquals(1,FilterRuleParser.getFilterStrings(strs,"@==:<jianglei>").size)
        Assert.assertEquals("jianglei",FilterRuleParser.getFilterStrings(strs,"@==:<jianglei>")[0])

        Assert.assertEquals(2,FilterRuleParser.getFilterStrings(strs,"@==:<jianglei,lei>").size)

        Assert.assertEquals(1,FilterRuleParser.getFilterStrings(strs,"@!=:<jianglei,lei>").size)
        Assert.assertEquals("longyi",FilterRuleParser.getFilterStrings(strs,"@!=:<jianglei,lei>")[0])
    }
}
