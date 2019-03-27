package com.jianglei.beautifulgirl.rule

import utils.JsonUtils

/**
 * @author jianglei on 3/23/19.
 */
class RuleCenter {
    companion object {
        private var isInit = false
        private val webRules = mutableListOf<WebRule>()

        fun getWebRules(): List<WebRule> {
            init()
            return webRules
        }

        fun init() {
            if (isInit) {
                return
            }
            val caoliu = "{\n" +
                    "  \"type\": \"image\",\n" +
                    "  \"name\": \"草榴\",\n" +
                    "  \"icon\": \"\",\n" +
                    "  \"encoding\": \"GBK\",\n" +
                    "  \"categoryRule\": {\n" +
                    "    \"url\": \"https://www.t66y.com/index.php\",\n" +
                    "    \"nameRule\": \"@class:<tr3 f_one> -> @label:<h2> ->@label:<a> -> @hasText:<新時代的我們,達蓋爾的旗幟> -> @text \",\n" +
                    "    \"urlRule\": \"@class:<tr3 f_one> -> @label:<h2> ->@label:<a> -> @hasText:<新時代的我們,達蓋爾的旗幟>->@property:<href>\"\n" +
                    "  },\n" +
                    "  \"coverRule\": {\n" +
                    "    \"dynamicRender\":true,\n" +
                    "    \"nameRule\": \"@class:<tr3 t_one tac> -> @class:<tal>[0]->@label:<h3>[0]->@label:<a>[0]->@text\",\n" +
                    "    \"descRule\": \"@class:<tr3 t_one tac>->@class:<tal>[0]->@text\",\n" +
                    "    \"urlRule\": \"@class:<tr3 t_one tac>->@class:<tal>[0]->@label:<h3>[0]->@label:<a>[0]->@property:<href>\",\n" +
                    "    \"pageRule\":{\n" +
                    "      \"fromHtml\":true,\n" +
                    "      \"nextUrlRule\":\"@class:<pages>[0] -> @label:<a> ->@hasText:<下一頁> -> @property:<href>\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"contentRule\": {\n" +
                    "    \"detailRule\": \"@label:<input>->@property:<data-src>\"\n" +
                    "  }\n" +
                    "}\n"
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

            val xvideos = "{\n" +
                    "  \"type\": \"video\",\n" +
                    "  \"name\": \"XVideos\",\n" +
                    "  \"icon\": \"https://www.xvideos.com/favicon.ico\",\n" +
                    "  \"encoding\": \"utf-8\",\n" +
                    "  \"categoryRule\": {\n" +
                    "    \"dynamicRender\": true,\n" +
                    "    \"url\": \"https://www.xvideos.com/channels-index\",\n" +
                    "    \"nameRule\": \"@class:<thumb-block > -> @class:<profile-name>[0] ->@label:<a>[0]->@text\",\n" +
                    "    \"urlRule\": \"@class:<thumb-block >->@class:<thumb>[0]->  @label:<a>[0]->@property:<href>\",\n" +
                    "    \"imageUrlRule\": \"@class:<thumb-block >->@class:<thumb>[0]->@label:<a>[0]->@label:<img>[0]->@property:<src> \",\n" +
                    "    \"descRule\": \"@class:<thumb-block >-> @class:<profile-counts>[0]->@text\",\n" +
                    "    \"pageRule\": {\n" +
                    "      \"fromHtml\": \"true\",\n" +
                    "      \"startPage\": \"0\",\n" +
                    "      \"nextUrlRule\": \"@class:<pagination>[0]->@label:<li>->@hasClass:<no-page next-page>->@label:<a>[0]->@property:<href>\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"coverRule\": {\n" +
                    "    \"dynamicRender\": false,\n" +
                    "    \"nameRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@text\",\n" +
                    "    \"descRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@class:<metadata>[0]->@text\",\n" +
                    "    \"imageUrlRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-inside>[0]->@class:<thumb>->@label:<img>[0]->@property:<data-src>\",\n" +
                    "    \"urlRule\": \"@class:<activity-event>->@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@property:<href>\",\n" +
                    "    \"realRequestUrlRule\": \"{baseUrl}/activity\",\n" +
                    "    \"pageRule\": {\n" +
                    "      \"fromHtml\": false,\n" +
                    "      \"combinedUrl\": \"{baseUrl}/{page}\",\n" +
                    "      \"paramRule\": \"@regex:<<!--[\\\\s*]([\\\\d]{10})[\\\\s*][-]{2}>[1]\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"searchRule\": {\n" +
                    "    \"searchUrl\": \"https://www.xvideos.com/?k={searchTxt}&top\",\n" +
                    "    \"resultRule\": {\n" +
                    "      \"dynamicRender\": false,\n" +
                    "      \"nameRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@text\",\n" +
                    "      \"descRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@class:<metadata>[0]->@text\",\n" +
                    "      \"imageUrlRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-inside>[0]->@class:<thumb>->@label:<img>[0]->@property:<data-src>\",\n" +
                    "      \"urlRule\": \"@class:<mozaique>->@class:<thumb-block>->@class:<thumb-under>[0]->@label:<a>[0]->@property:<href>\",\n" +
                    "      \"realRequestUrlRule\": \"{baseUrl}/activity\",\n" +
                    "      \"pageRule\": {\n" +
                    "        \"fromHtml\": \"true\",\n" +
                    "        \"startPage\": \"1\",\n" +
                    "        \"nextUrlRule\": \"@class:<pagination>[0]->@label:<li>->@label:<a>[0]->@hasText:<{page}>->@property:<href>\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"suggestUrl\": \"https://www.xvideos.com/search-suggest/{searchTxt}\",\n" +
                    "    \"suggestKeyRule\": \"@jsonArr:<KEYWORDS>->@jsonValue:<N>\",\n" +
                    "    \"suggestTimeRule\": \"@jsonArr:<KEYWORDS>->@jsonValue:<R>\"\n" +
                    "  },\n" +
                    "  \"contentRule\": {\n" +
                    "    \"dynamicRender\": true,\n" +
                    "    \"detailRule\": \"@regex:<setVideoUrlHigh\\\\('(.*?)'\\\\)>[1]\"\n" +
                    "  }\n" +
                    "}\n"
            webRules.add(JsonUtils.parseJsonWithGson(xvideos, WebRule::class.java)!!)
            webRules.clear()
            isInit = true

        }
    }
}