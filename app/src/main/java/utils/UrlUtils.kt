package utils

import java.net.URL

/**
 * @author jianglei on 1/24/19.
 */

class UrlUtils{
    companion object {
        /**
         * 获取一个路径的网站域名，包含协议加host
         */
        fun getWebHost(url:String):String{
            val tmp = URL(url)
            return if(url.startsWith("https")){
                "https://"+tmp.host
            }else{
                "http://"+tmp.host
            }
        }
    }
}
