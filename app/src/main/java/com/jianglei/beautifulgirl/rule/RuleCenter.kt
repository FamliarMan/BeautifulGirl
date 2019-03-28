package com.jianglei.beautifulgirl.rule

import utils.JsonUtils

/**
 * @author jianglei on 3/23/19.
 */
class RuleCenter {
    companion object {
        private var isInit = false
        private val webRules = mutableListOf<WebRule>()
        val caoliu = "{\"categoryRule\":{\"descRule\":\"\",\"dynamicRender\":false,\"imageUrlRule\":\"\",\"nameRule\":\"@class:\\u003ctr3 f_one\\u003e -\\u003e @label:\\u003ch2\\u003e -\\u003e@label:\\u003ca\\u003e -\\u003e @hasText:\\u003c新時代的我們,達蓋爾的旗幟\\u003e -\\u003e @text \",\"supportPage\":false,\"urlRule\":\"@class:\\u003ctr3 f_one\\u003e -\\u003e @label:\\u003ch2\\u003e -\\u003e@label:\\u003ca\\u003e -\\u003e @hasText:\\u003c新時代的我們,達蓋爾的旗幟\\u003e-\\u003e@property:\\u003chref\\u003e\",\"useWebView\":false},\"contentRule\":{\"dynamicRender\":false,\"nameRule\":\"\",\"realRequestUrlRule\":\"\",\"supportPage\":false,\"urlRule\":\"@label:\\u003cinput\\u003e-\\u003e@property:\\u003cdata-src\\u003e\",\"useWebView\":false},\"coverRule\":{\"descRule\":\"@class:\\u003ctr3 t_one tac\\u003e-\\u003e@class:\\u003ctal\\u003e[0]-\\u003e@text\",\"dynamicRender\":true,\"imageUrlRule\":\"\",\"nameRule\":\"@class:\\u003ctr3 t_one tac\\u003e -\\u003e @class:\\u003ctal\\u003e[0]-\\u003e@label:\\u003ch3\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@text\",\"pageRule\":{\"combinedUrl\":\"\",\"fromHtml\":true,\"nextUrlRule\":\"@class:\\u003cpages\\u003e[0] -\\u003e @label:\\u003ca\\u003e -\\u003e@hasText:\\u003c下一頁\\u003e -\\u003e @property:\\u003chref\\u003e\",\"paramRule\":\"\",\"startPage\":0},\"supportPage\":true,\"urlRule\":\"@class:\\u003ctr3 t_one tac\\u003e-\\u003e@class:\\u003ctal\\u003e[0]-\\u003e@label:\\u003ch3\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@property:\\u003chref\\u003e\",\"useWebView\":false},\"encoding\":\"GBK\",\"icon\":\"\",\"name\":\"草榴\",\"type\":\"image\",\"url\":\"https://www.t66y.com/index.php\"}"
        val xvideos = "{\"categoryRule\":{\"descRule\":\"@class:\\u003cthumb-block \\u003e-\\u003e @class:\\u003cprofile-counts\\u003e[0]-\\u003e@text\",\"dynamicRender\":true,\"imageUrlRule\":\"@class:\\u003cthumb-block \\u003e-\\u003e@class:\\u003cthumb\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@label:\\u003cimg\\u003e[0]-\\u003e@property:\\u003csrc\\u003e \",\"nameRule\":\"@class:\\u003cthumb-block \\u003e -\\u003e @class:\\u003cprofile-name\\u003e[0] -\\u003e@label:\\u003ca\\u003e[0]-\\u003e@text\",\"pageRule\":{\"combinedUrl\":\"\",\"fromHtml\":true,\"nextUrlRule\":\"@class:\\u003cpagination\\u003e[0]-\\u003e@label:\\u003cli\\u003e-\\u003e@hasClass:\\u003cno-page next-page\\u003e-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@property:\\u003chref\\u003e\",\"paramRule\":\"\",\"startPage\":0},\"supportPage\":true,\"urlRule\":\"@class:\\u003cthumb-block \\u003e-\\u003e@class:\\u003cthumb\\u003e[0]-\\u003e  @label:\\u003ca\\u003e[0]-\\u003e@property:\\u003chref\\u003e\",\"useWebView\":false},\"contentRule\":{\"dynamicRender\":true,\"nameRule\":\"\",\"realRequestUrlRule\":\"\",\"supportPage\":false,\"urlRule\":\"@regex:\\u003csetVideoUrlHigh\\\\(\\u0027(.*?)\\u0027\\\\)\\u003e[1]\",\"useWebView\":false},\"coverRule\":{\"descRule\":\"@class:\\u003cactivity-event\\u003e-\\u003e@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@class:\\u003cmetadata\\u003e[0]-\\u003e@text\",\"dynamicRender\":false,\"imageUrlRule\":\"@class:\\u003cactivity-event\\u003e-\\u003e@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-inside\\u003e[0]-\\u003e@class:\\u003cthumb\\u003e-\\u003e@label:\\u003cimg\\u003e[0]-\\u003e@property:\\u003cdata-src\\u003e\",\"nameRule\":\"@class:\\u003cactivity-event\\u003e-\\u003e@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@text\",\"pageRule\":{\"combinedUrl\":\"{baseUrl}/{page}\",\"fromHtml\":false,\"paramRule\":\"@regex:\\u003c\\u003c!--[\\\\s*]([\\\\d]{10})[\\\\s*][-]{2}\\u003e[1]\",\"startPage\":0},\"realRequestUrlRule\":\"{baseUrl}/activity\",\"supportPage\":true,\"urlRule\":\"@class:\\u003cactivity-event\\u003e-\\u003e@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@property:\\u003chref\\u003e\",\"useWebView\":false},\"encoding\":\"utf-8\",\"icon\":\"https://www.xvideos.com/favicon.ico\",\"name\":\"XVideos\",\"searchRule\":{\"dynamicRender\":false,\"resultRule\":{\"descRule\":\"@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@class:\\u003cmetadata\\u003e[0]-\\u003e@text\",\"dynamicRender\":false,\"imageUrlRule\":\"@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-inside\\u003e[0]-\\u003e@class:\\u003cthumb\\u003e-\\u003e@label:\\u003cimg\\u003e[0]-\\u003e@property:\\u003cdata-src\\u003e\",\"nameRule\":\"@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@text\",\"pageRule\":{\"combinedUrl\":\"\",\"fromHtml\":true,\"nextUrlRule\":\"@class:\\u003cpagination\\u003e[0]-\\u003e@label:\\u003cli\\u003e-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@hasText:\\u003c{page}\\u003e-\\u003e@property:\\u003chref\\u003e\",\"paramRule\":\"\",\"startPage\":1},\"realRequestUrlRule\":\"{baseUrl}/activity\",\"supportPage\":true,\"urlRule\":\"@class:\\u003cmozaique\\u003e-\\u003e@class:\\u003cthumb-block\\u003e-\\u003e@class:\\u003cthumb-under\\u003e[0]-\\u003e@label:\\u003ca\\u003e[0]-\\u003e@property:\\u003chref\\u003e\",\"useWebView\":false},\"searchUrl\":\"https://www.xvideos.com/?k\\u003d{searchTxt}\\u0026top\",\"suggestKeyRule\":\"@jsonArr:\\u003cKEYWORDS\\u003e-\\u003e@jsonValue:\\u003cN\\u003e\",\"suggestTimeRule\":\"@jsonArr:\\u003cKEYWORDS\\u003e-\\u003e@jsonValue:\\u003cR\\u003e\",\"suggestUrl\":\"https://www.xvideos.com/search-suggest/{searchTxt}\",\"supportSuggest\":true},\"supportSearch\":true,\"type\":\"video\",\"url\":\"https://www.xvideos.com/channels-index\"}\n"

        fun getWebRules(): List<WebRule> {
            init()
            return webRules
        }

        fun init() {
            if (isInit) {
                return
            }
            webRules.add(JsonUtils.parseJsonWithGson(caoliu, WebRule::class.java)!!)

            val fanli = "{\n" +
                    "  \"type\": \"image\",\n" +
                    "  \"name\": \"饭粒邪恶网\",\n" +
                    "  \"icon\": \"https://www.8mfh.com/wp-content/themes/xiu/images/logo.png\",\n" +
                    "  \"encoding\": \"GBK\",\n" +
                    "  \"categoryRule\": {\n" +
                    "    \"dynamicRender\":true,\n" +
                    "    \"url\": \"https://www.8mfh.com/\",\n" +
                    "    \"nameRule\": \"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @text\",\n" +
                    "    \"urlRule\": \"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @property:<href>\"\n" +
                    "  },\n" +
                    "  \"coverRule\": {\n" +
                    "    \"dynamicRender\":true,\n" +
                    "    \"nameRule\": \"@label:<article>->@label:<h2>[0]->@label:<a>->@text\",\n" +
                    "    \"descRule\": \"@label:<article> -> @class:<note>[0]->@text\",\n" +
                    "    \"imageUrlRule\":\"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@label:<img>->@property:<data-original>\",\n" +
                    "    \"urlRule\": \"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@property:<href>\",\n" +
                    "    \"pageRule\":{\n" +
                    "      \"fromHtml\":true,\n" +
                    "      \"nextUrlRule\":\"@class:<pagination pagination-multi>->@label:<li>->@label:<a>[0]->@hasText:<{page}>->@property:<href>\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"contentRule\": {\n" +
                    "    \"dynamicRender\":true,\n" +
                    "    \"detailRule\": \"@class:<article-content>[0]->@label:<img>->@property:<src>\",\n" +
                    "    \"pageRule\":{\n" +
                    "      \"fromHtml\":true,\n" +
                    "      \"nextUrlRule\":\"@class:<article-paging>->@label:<a>->@hasText:<{page}>->@property:<href>\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n"
            webRules.add(JsonUtils.parseJsonWithGson(fanli, WebRule::class.java)!!)

            webRules.add(JsonUtils.parseJsonWithGson(xvideos, WebRule::class.java)!!)
            webRules.clear()
            isInit = true

        }
    }
}