package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/31/19.
 */
class PropertyHandlerTest {

    @Test
    fun getPropertyRuleDesc() {
        var propertyHandler = PropertyHandler("@property:<name>")
        var propertyRule = propertyHandler.getRuleDesc()
        Assert.assertEquals("name", propertyRule.name)
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
        val elemens = listOf(document.selectFirst("li").selectFirst("a"))
        var propertyHandler = PropertyHandler("@property:<href>")
        var res = propertyHandler.handle(null,elemens)
        Assert.assertEquals(1,res.size)
        Assert.assertEquals("//hotmovs.com/",res[0])
    }
}