package com.jianglei.girlshow

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.jianglei.ruleparser.data.OnDataResultListener
import com.jianglei.videoplay.ContentVo
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.layout_recyclerview.*
import utils.DialogUtils

class PictureDetailListActivity : BaseActivity() {
    private var detailUrl: String? = null
    private var page = 1
    private var urls: MutableList<ContentVo> = ArrayList()
    private lateinit var adapter: PictureAdapter

    companion object {
        fun start(context: Context, detailUrl: String) {
            val intent = Intent(context, PictureDetailListActivity::class.java)
            intent.putExtra("detailUrl", detailUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recyclerview)
        detailUrl = intent.getStringExtra("detailUrl")
        initRecyclerview()
        rvContent.setRefreshing(true)
        getData()


    }

    private fun initRecyclerview() {
        adapter = PictureAdapter(this, urls)
        rvContent.setLinearLayout()
        rvContent.setAdapter(adapter)
        rvContent.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                getData()
            }

            override fun onRefresh() {
                page = 1
                urls.clear()
                rvContent.pullRefreshEnable = true
                getData()
            }

        })

    }

    private fun getData() {
        if (detailUrl == null) {
            return
        }
        StrategyProvider.getCurStrategy()!!
            .fetchAllContents(this, page, detailUrl,
                object : OnDataResultListener<List<ContentVo>> {
                    override fun onSuccess(data: List<ContentVo>) {

                        rvContent.post {
                            rvContent.setPullLoadMoreCompleted()
                        }
                        if (page == 1 && data.size == 0) {
                            DialogUtils.showTipDialog(this@PictureDetailListActivity, getString(R.string.no_picture))
                            return
                        } else if (data.size == 0) {
                            rvContent.pushRefreshEnable = false
                            Toast.makeText(this@PictureDetailListActivity, R.string.no_more_data, Toast.LENGTH_LONG)
                                .show()
                            return
                        }
                        urls.addAll(data)
                        if (page == 1) {
                            adapter.notifyDataSetChanged()
                        } else {
                            adapter.notifyItemInserted(urls.size - data.size)

                        }
                        page++

                        //第一次只加载了一张图片，无法出发上拉加载功能，因此这里静默多加载一次
                        if (page == 2 && urls.size == 1) {
                            getData()
                        }
                    }

                    override fun onError(msg: String) {

                        Toast.makeText(this@PictureDetailListActivity, msg, Toast.LENGTH_LONG).show()
                        rvContent.setPullLoadMoreCompleted()
                    }
                })
    }

    private class PictureAdapter(
        private var context: Context,
        private var urls: MutableList<ContentVo>
    ) :
        RecyclerView.Adapter<PictureHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_picture_detail, parent, false)
            return PictureHolder(view)
        }

        override fun getItemCount(): Int {
            return urls.size
        }

        override fun onBindViewHolder(holder: PictureHolder, position: Int) {
            val controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(urls[position].url))
                .setAutoPlayAnimations(true)
                .build()
            holder.ivContent.controller = (controller)
            holder.ivContent.setOnClickListener {
                val intent = Intent(context, PhotoViewActivity::class.java)
                val urlStrins = urls.map{
                    it.url
                }
                intent.putStringArrayListExtra("urls", urlStrins as java.util.ArrayList<String>?)
                intent.putExtra("pos",position)
                context.startActivity(intent)
            }
//            val options = RequestOptions()
//                .placeholder(R.mipmap.holder_picture)
//                .centerCrop()
//
//                .dontAnimate()
//            Glide.with(context)
//                .asGif()
//                .load(urls[position])
//                .apply(options)
//                .into(holder.ivContent)
        }

    }

    private class PictureHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivContent: SimpleDraweeView = view.findViewById(R.id.ivCover)
    }

    override fun onPause() {
        super.onPause()
    }
}

