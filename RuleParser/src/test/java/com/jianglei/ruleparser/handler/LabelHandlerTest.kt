package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/31/19.
 */
class LabelHandlerTest {

    @Test
    fun getLabelRuleDesc() {
        var labelHandler = LabelHandler("@label:<name>")
        var labelRule = labelHandler.getRuleDesc()
        Assert.assertEquals("name", labelRule.name)


        labelHandler = LabelHandler("@label:<name>[0]")
        labelRule = labelHandler.getRuleDesc()
        Assert.assertEquals("name", labelRule.name)
        Assert.assertEquals(0, labelRule.index)
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
        val labelHandler = LabelHandler("@label:<li>")
        val res = labelHandler.handle(null,elemens)
        Assert.assertEquals(2,res.size)
    }
}