package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.vo.WebsiteVo

/**
 * @author jianglei on 1/6/19.
 */
object WebsiteCenter {
    /**
     * 国内能正常访问的网站
     */
    var allNormalWebSites: ArrayList<WebsiteVo> = ArrayList()
    /**
     * 国内需要vpn访问的网站
     */
    var allVpnWebSites: ArrayList<WebsiteVo> = ArrayList()

    init {
        val websiteVo = WebsiteVo(
            "饭粒邪恶网",
            "https://www.retuwo.com/",
            R.mipmap.favicon,
            DataSourceCenter.SOURCE_FANLI_PICTURE
        )
        allNormalWebSites.add(websiteVo)
    }

    fun getAllNormalWebsites(): ArrayList<WebsiteVo> {
        return allNormalWebSites
    }
}