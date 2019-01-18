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

        var websiteVo = WebsiteVo(
            "94套图网",
            "http://www.94xxx.pw/",
            R.mipmap.ninefour,
            DataSourceCenter.SOURCE_94_TAOTU_PICTURE,
            "图片"
        )
        allNormalWebSites.add(websiteVo)
        websiteVo = WebsiteVo(
            "饭粒邪恶网",
            "https://www.retuwo.com/",
            R.mipmap.favicon,
            DataSourceCenter.SOURCE_FANLI_PICTURE,
            "图片"
        )
        allNormalWebSites.add(websiteVo)

        websiteVo = WebsiteVo(
            "妆秀性感美女图片",
            "http://www.zhuangxiule.cn/",
            R.mipmap.ic_zhuangxiu,
            DataSourceCenter.SOURCE_ZHUANGXIU_PICTURE,
            "图片"
        )
        allNormalWebSites.add(websiteVo)


        //下面开始注册需要vpn的网站
        websiteVo = WebsiteVo(
            "草榴",
            "https://www.t66y.com/index.php",
            R.mipmap.caoliu,
            DataSourceCenter.SOURCE_CAOLIU_PICTURE,
            "图片"
        )

        allVpnWebSites.add(websiteVo)

        websiteVo = WebsiteVo(
            "91自拍",
            "http://www.91porn.com/v.php?next=watch",
            R.mipmap.ic_91,
            DataSourceCenter.SOURCE_91_VIDEO,
            "视频"
        )
        allVpnWebSites.add(websiteVo)
        websiteVo = WebsiteVo(
            "xvideos",
            "https://www.xvideos.com/channels-index",
            R.mipmap.xvideos,
            DataSourceCenter.SOURCE_XVIDEOS_VIDE,
            "视频"
        )
        allVpnWebSites.add(websiteVo)

        websiteVo = WebsiteVo(
            "pornhub",
            "https://www.pornhub.com/categories",
            R.mipmap.pornhub,
            DataSourceCenter.SOURCE_PORNHUB_VIDEO,
            "视频"
        )
        allVpnWebSites.add(websiteVo)
    }

    fun getAllNormalWebsites(): ArrayList<WebsiteVo> {
        return allNormalWebSites
    }

    fun getAllVpnWebsites(): ArrayList<WebsiteVo> {
        return allVpnWebSites
    }
}