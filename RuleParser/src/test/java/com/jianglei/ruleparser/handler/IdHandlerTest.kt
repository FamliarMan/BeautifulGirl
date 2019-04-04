package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/31/19.
 */
class IdHandlerTest {

    @Test
    fun getIdRuleDesc() {
        var idHandler = IdHandler("@id:<name>")
        var idRule = idHandler.getRuleDesc()
        Assert.assertEquals("name", idRule.name)


        idHandler = IdHandler("@id:<name>[0]")
        idRule = idHandler.getRuleDesc()
        Assert.assertEquals("name", idRule.name)
        Assert.assertEquals(0, idRule.index)


        idHandler = IdHandler("@id:regex<name>[0]")
        idRule = idHandler.getRuleDesc()
        Assert.assertEquals(null, idRule.name)
        Assert.assertEquals("name", idRule.regx)
        Assert.assertEquals(0, idRule.index)
    }


    @Test
    fun handle(){

        val html = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body id=\"face-search\">\n" +
                "\n" +
                "<div id=\"tradeindex_wrapper\">\n" +
                "    <h2>More free porn</h2>\n" +
                "    <div id=\"freemore\">\n" +
                "        <ol start=\"1\" id=\"globaltop col1\">\n" +
                "            <li><a href=\"//hotmovs.com/\" target=\"_blank\">Hot Movs</a></li>\n" +
                "            <li><a href=\"//www.txxx.com/\" target=\"_blank\">Txxx</a></li>\n" +
                "        </ol>\n" +
                "        <div id=\"clearfloat\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"
        val document = Jsoup.parse(html)
        val elemens = listOf(document)
        var idHandler = IdHandler("@id:regex<free>")
        var res = idHandler.handle(null,elemens)
        Assert.assertEquals(1,res.size)
        idHandler = IdHandler("@id:<freemore>")
        res = idHandler.handle(null,elemens)
        Assert.assertEquals(1,res.size)
    }
}