package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/31/19.
 */
class ClassHandlerTest {

    @Test
    fun getClassRuleDesc() {
        var classHandler = ClassHandler("@class:<name>")
        var classRule = classHandler.getRuleDesc()
        Assert.assertEquals("name", classRule.name)


        classHandler = ClassHandler("@class:<name>[0]")
        classRule = classHandler.getRuleDesc()
        Assert.assertEquals("name", classRule.name)
        Assert.assertEquals(0, classRule.index)


        classHandler = ClassHandler("@class:regex<name>[0]")
        classRule = classHandler.getRuleDesc()
        Assert.assertEquals(null, classRule.name)
        Assert.assertEquals("name", classRule.regx)
        Assert.assertEquals(0, classRule.index)
    }


    @Test
    fun handle(){

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
        val document = Jsoup.parse(html)
        val elemens = listOf(document)
        var classHandler = ClassHandler("@class:regex<free>")
        val res = classHandler.handle()
    }
}