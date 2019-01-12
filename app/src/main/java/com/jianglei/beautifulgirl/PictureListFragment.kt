package com.jianglei.beautifulgirl

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.video.VideoPlayActivity
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView

/**
 * @author jianglei on 1/3/19.
 */
class PictureListFragment : Fragment() {
    private var contentTitles: MutableList<ContentTitle> = ArrayList()
    private var dataSource: DataSource? = null
    private var page = 1
    private var visible: Boolean = false
    private var isPrepared: Boolean = false
    private var isLoaded: Boolean = false
    private lateinit var titleAdapter: PictureTitleAdapter
    private lateinit var rvContent: PullLoadMoreRecyclerView
    private var title: String? = null
    private var url: String? = null
    private var dataSourceKey: String? = null

    companion object {
        fun newInstance(title: String, url: String, dataSourceKey: String,isFromActivity:Boolean=false): PictureListFragment {
            val fragment = PictureListFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("url", url)
            bundle.putString("dataSourceKey", dataSourceKey)
            bundle.putBoolean("isFromActivity",isFromActivity)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        title = arguments?.getString("title")
        url = arguments?.getString("url")
        val view = inflater.inflate(R.layout.fragment_picture_list, container, false)
        dataSourceKey = arguments?.getString("dataSourceKey")
        if (dataSourceKey == null) {
            return view
        }
        dataSource = DataSourceCenter.getDataSource(dataSourceKey!!)
        if (dataSource == null) {
            return view
        }
        rvContent = view.findViewById(R.id.rvContent)
        initRecyclerview()
        isPrepared = true
        val isFromActivity = arguments?.getBoolean("isFromActivity")
        if(isFromActivity!!){
            visible = true
        }
        lazyLoad()
        return view
    }

    private fun initRecyclerview() {
        titleAdapter = PictureTitleAdapter(context!!, contentTitles)
        titleAdapter.onItemClickListener = object : PictureTitleAdapter.OnItemClickListener {
            override fun onItemClick(title: ContentTitle, pos: Int) {
                if (title.type == Category.TYPE_VIDEO) {
                    val intent = Intent(activity, VideoPlayActivity::class.java)
                    intent.putExtra("detailUrl", title.detailUrl)
                    intent.putExtra("dataSourceKey", dataSourceKey)
                    startActivity(intent)
                } else {
                    val intent = Intent(activity, PictureDetailListActivity::class.java)
                    intent.putExtra("detailUrl", title.detailUrl)
                    intent.putExtra("dataSourceKey", dataSourceKey)
                    startActivity(intent)
                }

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
                contentTitles.clear()
                rvContent.pullRefreshEnable = true
                fetchData()
            }

        })

    }

    private fun fetchData() {
        if (url == null) {
            return
        }
        dataSource!!.fetchTitles(url!!, page, object : OnDataResultListener<MutableList<ContentTitle>> {
            override fun onSuccess(data: MutableList<ContentTitle>) {
                rvContent.setPullLoadMoreCompleted()
                if (data.size == 0) {
                    rvContent.pushRefreshEnable = false
                    Toast.makeText(activity, R.string.no_more_data, Toast.LENGTH_LONG).show()
                    return
                }
                data.forEach{
                    Log.d("jianglei",it.title+"  "+it.coverUrl)
                }

                contentTitles.addAll(data)
                if (page == 1) {
                    titleAdapter.notifyDataSetChanged()
                } else {
                    titleAdapter.notifyItemInserted(contentTitles.size - data.size)

                }

                page++
            }

            override fun onError(msg: String) {
                rvContent.setPullLoadMoreCompleted()
                if (context == null) {
                    return
                }
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

    override fun onPause() {
        super.onPause()
        if (isRemoving) {
            dataSource?.cancelAllNet()
        }
    }
}