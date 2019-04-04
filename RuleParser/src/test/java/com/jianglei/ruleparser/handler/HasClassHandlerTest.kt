package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/4/19.
 */
class HasClassHandlerTest {

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
        var handler = HasClassHandler("@hasClass:<freemore>")
        var res = handler.handle(null,preElements)
        Assert.assertEquals(1,res.size)
        handler = HasClassHandler("@hasClass:<emore>")
        res = handler.handle(null,preElements)
        Assert.assertEquals(0,res.size)


//        res = FilterRuleParser.getFilterElements(preElements,"@noClass:<emore>")
//        Assert.assertEquals(1,res.size)
//
//        res = FilterRuleParser.getFilterElements(preElements,"@noClass:<freemore>")
//        Assert.assertEquals(0,res.size)
    }
}