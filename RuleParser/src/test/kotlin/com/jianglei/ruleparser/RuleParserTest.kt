package com.jianglei.ruleparser

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.junit.Test

/**
 * @author jianglei on 3/16/19.
 */
class RuleParserTest {

    @Test
    fun getClassElementsByName(){
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
                "            <li><a href=\"//www.hclips.com/\" target=\"_blank\">HClips</a></li>\n" +
                "            <li><a href=\"//vjav.com/\" target=\"_blank\">Japanese Porn</a></li>\n" +
                "            <li><a href=\"//pornzog.com/\" target=\"_blank\">Porn Zog</a></li>\n" +
                "            <li><a href=\"//upornia.com/\" target=\"_blank\">Upornia</a></li>\n" +
                "            <li><a href=\"//www.hdzog.com/\" target=\"_blank\">HD Porn</a></li>\n" +
                "            <li><a href=\"//voyeurhit.com/\" target=\"_blank\">VoyeurHit</a></li>\n" +
                "            <li><a href=\"//100voyeur.com/\" target=\"_blank\">100 Voyeur</a></li>\n" +
                "            <li><a href=\"//upskirtcollection.com/\" target=\"_blank\">Upskirt Collection</a></li>\n" +
                "            <li><a href=\"//100upskirts.com/\" target=\"_blank\">100 Upskirts</a></li>\n" +
                "            <li><a href=\"//ipornia.com/\" target=\"_blank\">IPornia Porn Tube</a></li>\n" +
                "            <li><a href=\"//tuberb.com/\" target=\"_blank\">Free Porn Tube</a></li>\n" +
                "            <li><a href=\"//imagezog.com/\" target=\"_blank\">Image Zog</a></li>\n" +
                "            <li><a href=\"//tuberl.com/\" target=\"_blank\">TubeRL</a></li>\n" +
                "            <li><a href=\"//theclassicporn.com/\" target=\"_blank\">The Classic Porn</a></li>\n" +
                "            <li><a href=\"//tubepornclassic.com/\" target=\"_blank\">Tube Porn Classic</a></li>\n" +
                "            <li><a href=\"//wtfpass.com/\" target=\"_blank\">Reality Porn</a></li>\n" +
                "            <li><a href=\"//collegefuckparties.com/\" target=\"_blank\">College Fuck Parties</a></li>\n" +
                "            <li><a href=\"//www.hentaivideoworld.com/\" target=\"_blank\">Hentai Video World</a></li>\n" +
                "            <li><a href=\"//thegay.com/\" target=\"_blank\">Free Gay Porn</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"

        val document = Jsoup.parse(html)
        val parser = RuleParser(document)
        var url = parser.getStrings("@class:<freemore> -> @label:<li>[0]->@label:<a>->@property:<href>")
        url = parser.getStrings("@class:<freemore> -> @label:<li>[0]->@label:<a>->@property:<href>")
        System.out.println(url.toString())

    }

    @Test
    fun getClassElementsByRegx(){
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
                "            <li><a href=\"//www.hclips.com/\" target=\"_blank\">HClips</a></li>\n" +
                "            <li><a href=\"//vjav.com/\" target=\"_blank\">Japanese Porn</a></li>\n" +
                "            <li><a href=\"//pornzog.com/\" target=\"_blank\">Porn Zog</a></li>\n" +
                "            <li><a href=\"//upornia.com/\" target=\"_blank\">Upornia</a></li>\n" +
                "            <li><a href=\"//www.hdzog.com/\" target=\"_blank\">HD Porn</a></li>\n" +
                "            <li><a href=\"//voyeurhit.com/\" target=\"_blank\">VoyeurHit</a></li>\n" +
                "            <li><a href=\"//100voyeur.com/\" target=\"_blank\">100 Voyeur</a></li>\n" +
                "            <li><a href=\"//upskirtcollection.com/\" target=\"_blank\">Upskirt Collection</a></li>\n" +
                "            <li><a href=\"//100upskirts.com/\" target=\"_blank\">100 Upskirts</a></li>\n" +
                "            <li><a href=\"//ipornia.com/\" target=\"_blank\">IPornia Porn Tube</a></li>\n" +
                "            <li><a href=\"//tuberb.com/\" target=\"_blank\">Free Porn Tube</a></li>\n" +
                "            <li><a href=\"//imagezog.com/\" target=\"_blank\">Image Zog</a></li>\n" +
                "            <li><a href=\"//tuberl.com/\" target=\"_blank\">TubeRL</a></li>\n" +
                "            <li><a href=\"//theclassicporn.com/\" target=\"_blank\">The Classic Porn</a></li>\n" +
                "            <li><a href=\"//tubepornclassic.com/\" target=\"_blank\">Tube Porn Classic</a></li>\n" +
                "            <li><a href=\"//wtfpass.com/\" target=\"_blank\">Reality Porn</a></li>\n" +
                "            <li><a href=\"//collegefuckparties.com/\" target=\"_blank\">College Fuck Parties</a></li>\n" +
                "            <li><a href=\"//www.hentaivideoworld.com/\" target=\"_blank\">Hentai Video World</a></li>\n" +
                "            <li><a href=\"//thegay.com/\" target=\"_blank\">Free Gay Porn</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"

        val document = Jsoup.parse(html)
        val parser = RuleParser(document)
        var url = parser.getStrings("@class:regex<fr[e]{2}more> -> @label:<li>[0]->@label:<a>->@property:<href>")
        System.out.println(url.toString())
    }

    @Test
    fun getRegex(){
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
                "            <li><a href=\"//www.hclips.com/\" target=\"_blank\">HClips</a></li>\n" +
                "            <li><a href=\"//vjav.com/\" target=\"_blank\">Japanese Porn</a></li>\n" +
                "            <li><a href=\"//pornzog.com/\" target=\"_blank\">Porn Zog</a></li>\n" +
                "            <li><a href=\"//upornia.com/\" target=\"_blank\">Upornia</a></li>\n" +
                "            <li><a href=\"//www.hdzog.com/\" target=\"_blank\">HD Porn</a></li>\n" +
                "            <li><a href=\"//voyeurhit.com/\" target=\"_blank\">VoyeurHit</a></li>\n" +
                "            <li><a href=\"//100voyeur.com/\" target=\"_blank\">100 Voyeur</a></li>\n" +
                "            <li><a href=\"//upskirtcollection.com/\" target=\"_blank\">Upskirt Collection</a></li>\n" +
                "            <li><a href=\"//100upskirts.com/\" target=\"_blank\">100 Upskirts</a></li>\n" +
                "            <li><a href=\"//ipornia.com/\" target=\"_blank\">IPornia Porn Tube</a></li>\n" +
                "            <li><a href=\"//tuberb.com/\" target=\"_blank\">Free Porn Tube</a></li>\n" +
                "            <li><a href=\"//imagezog.com/\" target=\"_blank\">Image Zog</a></li>\n" +
                "            <li><a href=\"//tuberl.com/\" target=\"_blank\">TubeRL</a></li>\n" +
                "            <li><a href=\"//theclassicporn.com/\" target=\"_blank\">The Classic Porn</a></li>\n" +
                "            <li><a href=\"//tubepornclassic.com/\" target=\"_blank\">Tube Porn Classic</a></li>\n" +
                "            <li><a href=\"//wtfpass.com/\" target=\"_blank\">Reality Porn</a></li>\n" +
                "            <li><a href=\"//collegefuckparties.com/\" target=\"_blank\">College Fuck Parties</a></li>\n" +
                "            <li><a href=\"//www.hentaivideoworld.com/\" target=\"_blank\">Hentai Video World</a></li>\n" +
                "            <li><a href=\"//thegay.com/\" target=\"_blank\">Free Gay Porn</a></li>\n" +
                "        </ol>\n" +
                "        <div class=\"clearfloat\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n"

        val document = Jsoup.parse(html)
        val parser = RuleParser(document)
        var url = parser.getStrings("@regex:<<body class=\"(.*)\">>[1]")
        System.out.println(url.toString())
    }

}