package utils

import java.net.URL

/**
 * @author jianglei on 1/24/19.
 */

class UrlUtils {
    companion object {
        /**
         * 获取一个路径的网站域名，包含协议加host
         */
        fun getWebHost(url: String): String {
            val tmp = URL(url)
            return if (url.startsWith("https")) {
                "https://" + tmp.host
            } else {
                "http://" + tmp.host
            }
        }

        /**
         * 获取一个网址的协议
         */
        fun getWebProtocol(url: String): String {
            return if (url.startsWith("https")) {
                "https"
            } else {
                "http"
            }
        }

        fun getFullUrl(host: String, protocol: String, url: String): String {
            return when {
                url.startsWith("http") -> url
                url.startsWith("//") -> protocol + url
                url.startsWith("/") -> host+url
                else -> "$host/$url"
            }
        }
    }
}
