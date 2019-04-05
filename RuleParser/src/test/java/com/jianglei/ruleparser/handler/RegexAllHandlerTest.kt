package com.jianglei.ruleparser.handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 4/5/19.
 */
class RegexAllHandlerTest {
    @Test
    fun getRegexAll(){
        val html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<!--绿色导航-->\n" +
                "<div class=\"container\">\n" +
                "    <!-- 亚洲裸女 -->\n" +
                "    <a name=\"mh-vip-01\" class=\"mh-anchor\"></a>\n" +
                "    <div class=\"title-content\">\n" +
                "        <div class=\"title-bestalbum\"><img src=\"https://static.nvshenim.info/static/image/common//image/yazhouluonv.png\"></div>\n" +
                "    </div>\n" +
                "    <!-- 欧洲裸女 -->\n" +
                "    <a name=\"mh-vip-02\" class=\"mh-anchor\"></a>\n" +
                "    <div class=\"title-content\">\n" +
                "        <div class=\"title-bestalbum\"><img src=\"https://static.nvshenim.info/static/image/common//image/ouzhouluonv.png\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"

        val handler = RegexAllHandler("@regexAll:<<!--[\\s*](.*)[\\s*][-]{2}>")
        val document = Jsoup.parse(html)
        val preResult = listOf<Element>(document.getElementsByClass("container").first())
        val res:List<String> = handler.handle(null,preResult) as List<String>
        Assert.assertEquals(2,res.size)

    }
}