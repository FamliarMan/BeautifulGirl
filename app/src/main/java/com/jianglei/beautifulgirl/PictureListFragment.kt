package com.jianglei.beautifulgirl

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.PictureDataProvider
import com.jianglei.beautifulgirl.spider.PictureTitleVo
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView

/**
 * @author jianglei on 1/3/19.
 */
class PictureListFragment : Fragment() {
    private var pictureTitles: MutableList<PictureTitleVo> = ArrayList()
    private var dataProvider = PictureDataProvider()
    private var page = 1
    private var visible: Boolean = false
    private var isPrepared: Boolean = false
    private var isLoaded :Boolean = false
    private lateinit var titleAdapter: PictureTitleAdapter
    private lateinit var rvContent: PullLoadMoreRecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_picture_list, container, false)
        rvContent = view.findViewById(R.id.rvContent)
        initRecyclerview()
        isPrepared = true
        lazyLoad()
        return view
    }

    private fun initRecyclerview() {
        titleAdapter = PictureTitleAdapter(context!!, pictureTitles)
        titleAdapter.onItemClickListener = object : PictureTitleAdapter.OnItemClickListener {
            override fun onItemClick(titleVo: PictureTitleVo, pos: Int) {
                val intent = Intent(activity, PictureDetailListActivity::class.java)
                intent.putExtra("detailUrl", titleVo.detailUrl)
                startActivity(intent)

            }

        }
        rvContent.setLinearLayout()
        rvContent.setAdapter(titleAdapter)

        rvContent.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                fetchData()
            }

            override fun onRefresh() {
                page = 1
                pictureTitles.clear()
                fetchData()
            }

        })

    }

    private fun fetchData() {
        dataProvider.fetchData(page, object : OnDataResultListener<MutableList<PictureTitleVo>> {
            override fun onSuccess(data: MutableList<PictureTitleVo>) {
                rvContent.setPullLoadMoreCompleted()
                if (data.size == 0){
                    rvContent.pushRefreshEnable=false
                    Toast.makeText(activity,R.string.no_more_data,Toast.LENGTH_LONG).show()
                    return
                }

                pictureTitles.addAll(data)
                titleAdapter.notifyDataSetChanged()

                page++
            }

            override fun onError(msg: String) {
                rvContent.setPullLoadMoreCompleted()
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun lazyLoad() {
        if (isPrepared && visible && !isLoaded) {
            rvContent.setRefreshing(true)
            fetchData()
            isLoaded = true
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            visible = true
            lazyLoad()
        }
    }
}