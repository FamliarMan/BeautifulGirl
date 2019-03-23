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
                    "    \"targetUrlRule\": \"@class:<tr3 t_one tac>->@class:<tal>[0]->@label:<h3>[0]->@label:<a>[0]->@property:<href>\",\n" +
                    "    \"pageRule\":{\n" +
                    "      \"isFromHtml\":true,\n" +
                    "      \"nextUrlRule\":\"@class:<pages>[0] -> @label:<a> ->@hasText:<下一頁> -> @property:<href>\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"contentRule\": {\n" +
                    "    \"detailRule\": \"@label:<input>->@property:<data-src>\"\n" +
                    "  }\n" +
                    "}\n"
            webRules.add(JsonUtils.parseJsonWithGson(caoliu, WebRule::class.java)!!)
            isInit = true

        }
    }
}