package com.jianglei.beautifulgirl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.WebDataSource
import com.jianglei.beautifulgirl.data.WebVideoSource
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import com.jianglei.beautifulgirl.vo.PlayContent
import com.jianglei.videoplay.VideoPlayActivity
import com.jianglei.videoplay.WebViewPlayActivity
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import utils.ToastUtils

/**
 * 所有封面内容的展示，包括视频的或图片的
 * 可以从这个页面进入详情页
 * @author jianglei on 1/3/19.
 */
class ContentCoverListFragment : BaseFragment() {
    private var contentTitles: MutableList<ContentTitle> = ArrayList()
    private var webDataSource: WebDataSource? = null
    private var page = 1
    private var visible: Boolean = false
    private var isPrepared: Boolean = false
    private var isLoaded: Boolean = false
    private lateinit var titleAdapter: PictureTitleAdapter
    private lateinit var rvContent: PullLoadMoreRecyclerView
    private var title: String? = null
    private var url: String? = null

    companion object {
        fun newInstance(
            title: String,
            url: String,
            dataSource: WebDataSource,
            isFromActivity: Boolean = false
        ): ContentCoverListFragment {
            val fragment = ContentCoverListFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("url", url)
            bundle.putSerializable("dataSourceId", dataSource.id)
            bundle.putBoolean("isFromActivity", isFromActivity)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        title = arguments?.getString("title")
        url = arguments?.getString("url")
        val view = inflater.inflate(R.layout.fragment_picture_list, container, false)
        val dataSourceId = arguments?.getString("dataSourceId")
        webDataSource = WebSourceCenter.getWebSource(dataSourceId!!)
        if (webDataSource == null) {
            return view
        }
        rvContent = view.findViewById(R.id.rvContent)
        initRecyclerview()
        isPrepared = true
        val isFromActivity = arguments?.getBoolean("isFromActivity")
        if (isFromActivity!!) {
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
                    if(title.isUseWeb){
                        //只能使用web浏览
                        val intent = Intent(activity, WebViewPlayActivity::class.java)
                        intent.putExtra("playUrl", title.detailUrl)
                        startActivity(intent)
                        return
                    }
                    try {
                        getVideoPlayUrl(title.detailUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (activity != null) {
                            ToastUtils.showMsg(activity!!, e.localizedMessage)
                        }
                    }
                } else {
                    val intent = Intent(activity, PictureDetailListActivity::class.java)
                    intent.putExtra("detailUrl", title.detailUrl)
                    intent.putExtra("dataSourceId", webDataSource!!.id)
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

    private fun getVideoPlayUrl(detailUrl: String) {
        showProgress(true)

        val webVideoSource = webDataSource as WebVideoSource
        webVideoSource.fetchVideoUrls(activity!!,detailUrl, object : OnDataResultListener<MutableList<PlayContent>> {
            override fun onSuccess(data: MutableList<PlayContent>) {
                showProgress(false)
                if (data.size > 1) {
                    //有多个播放地址，要进入播放列表选择页
                    val intent = Intent(activity, PlayContentListActivity::class.java)
                    intent.putExtra("playContents", Gson().toJson(data))
                    startActivity(intent)
                } else {
                    //只有一个播放地址选择合适的播放地址
                    val playContent = data[0]
                    val playUrls = playContent.file
                    var playUrl: String? = null
                    playUrls!!.forEach {
                        if (it.defaultQuality) {
                            playUrl = it.videoUrl
                        }
                    }
                    val intent = Intent(activity, VideoPlayActivity::class.java)
                    intent.putExtra("playUrl", playUrl)
                    startActivity(intent)
                }
            }

            override fun onError(msg: String) {
                if (activity == null) {
                    return
                }
                showProgress(false)
                ToastUtils.showMsg(activity!!.applicationContext, getString(R.string.get_video_play_url_error))
            }
        })


    }

    private fun fetchData() {
        if (url == null) {
            return
        }
        webDataSource!!.fetchCoverContents(activity!!,url!!, page, object : OnDataResultListener<MutableList<ContentTitle>> {
            override fun onSuccess(data: MutableList<ContentTitle>) {
                if (data.size == 0) {
                    rvContent.pushRefreshEnable = false
                    Toast.makeText(activity, R.string.no_more_data, Toast.LENGTH_LONG).show()
                    rvContent.post {
                        rvContent.setPullLoadMoreCompleted()
                    }
                    return
                }
                data.forEach {
                    Log.d("jianglei", it.title + "  " + it.coverUrl)
                }

                contentTitles.addAll(data)
                if (page == 1) {
                    titleAdapter.notifyDataSetChanged()
                } else {
                    titleAdapter.notifyItemInserted(contentTitles.size - data.size)

                }

                page++
                rvContent.post {
                    Log.d("longyi","cloase loadiing")
                    rvContent.setPullLoadMoreCompleted()
                }
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
            webDataSource?.cancelAllNet()
        }
    }
}