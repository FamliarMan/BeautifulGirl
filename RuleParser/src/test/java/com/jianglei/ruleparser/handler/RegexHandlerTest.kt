package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 3/31/19.
 */
class RegexHandlerTest {

    @Test
    fun getRegexRuleDesc() {


        var regexHandler= RegexHandler("@regex:<p>")
        var regexRule = regexHandler.getRuleDesc()
        Assert.assertEquals("p",regexRule.regx)
        Assert.assertEquals(null,regexRule.index)

        regexHandler= RegexHandler("@regex:<<!--[\\\\s*]([\\\\d]{10})[\\\\s*]-->>[1]")
        regexRule = regexHandler.getRuleDesc()
        Assert.assertEquals("<!--[\\\\s*]([\\\\d]{10})[\\\\s*]-->",regexRule.regx)



        regexHandler= RegexHandler("@regex:<p>[0]")
        regexRule = regexHandler.getRuleDesc()

        Assert.assertEquals("p",regexRule.name)
        Assert.assertEquals("p",regexRule.regx)
        Assert.assertEquals(0,regexRule.index)
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
        val regexHandler = RegexHandler("@regex:<<body class=(.*)>>[1]")
        val res = regexHandler.handle(null, listOf(html))
        Assert.assertEquals(1,res.size)
        Assert.assertEquals("\"face-search\"",res[0])
    }
}