package com.jianglei.beautifulgirl.data

import com.jianglei.beautifulgirl.spider.PictureTitleVo

/**
 * @author jianglei on 1/3/19.
 */
class PictureDataProvider : DataProvider {
    override fun fetchDetailList(url: String, page:Int,listener: OnDataResultListener<MutableList<String>>) {
        DataSourceCenter.getDataSource(curDataSource)?.fetDetailPictures(url,page, listener)

    }

    override fun fetchData(page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>) {
        DataSourceCenter.getDataSource(curDataSource)?.fetchTitles(
            page,
            listener
        )
    }

    private val dataSource = listOf(DataSourceCenter.SOURCE_FANLI_PICTURE)
    private var curDataSource: String = dataSource[0]
    override fun canChangeDataSource(): Boolean {
        //暂时只能从爬虫获取
        return false
    }


    override fun changeDataSource(dataSource: DataSource) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//    override fun  fetchData(page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>) {
//    }
}