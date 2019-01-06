package com.jianglei.beautifulgirl

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.PictureDataProvider
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.layout_recyclerview.*

class PictureDetailListActivity : BaseActivity() {
    private var dataProvider = PictureDataProvider()
    private var detailUrl: String? = null
    private var page = 1
    private var urls:MutableList<String> = ArrayList()
    private lateinit var adapter: PictureAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recyclerview)
        detailUrl = intent.getStringExtra("detailUrl")
        initRecyclerview()
        rvContent.setRefreshing(true)
        getData()


    }

    private fun initRecyclerview() {
        adapter= PictureAdapter(this, urls)
        rvContent.setLinearLayout()
        rvContent.setAdapter(adapter)
        rvContent.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onLoadMore() {
                getData()
            }

            override fun onRefresh() {
                page = 1
                urls.clear()
                getData()
            }

        })

    }
    private fun getData() {
        if (detailUrl == null) {
            return
        }
        dataProvider.fetchDetailList(detailUrl!!, page,object : OnDataResultListener<MutableList<String>>{
            override fun onError(msg: String) {
                Toast.makeText(this@PictureDetailListActivity,msg,Toast.LENGTH_LONG).show()
                rvContent.setPullLoadMoreCompleted()
            }

            override fun onSuccess(data: MutableList<String>) {
                rvContent.setPullLoadMoreCompleted()
                if (data.size == 0){
                    rvContent.pushRefreshEnable=false
                    Toast.makeText(this@PictureDetailListActivity,R.string.no_more_data,Toast.LENGTH_LONG).show()
                    return
                }
                urls.addAll(data)
                adapter.notifyDataSetChanged()
                page++
            }

        })
    }
    private class PictureAdapter(private var context:Context,private var urls:MutableList<String>)
        : RecyclerView.Adapter<PictureHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_picture_detail,parent,false)
            return PictureHolder(view)
        }

        override fun getItemCount(): Int {
            return urls.size
        }

        override fun onBindViewHolder(holder: PictureHolder, position: Int) {
            val options = RequestOptions()
                .placeholder(R.mipmap.holder_picture)
                .centerCrop()
                .dontAnimate()
            Glide.with(context)
                .load(urls[position])
                .apply(options)
                .into(holder.ivContent)
        }

    }
    private class PictureHolder(view: View):RecyclerView.ViewHolder(view){
        var ivContent: ImageView = view.findViewById(R.id.ivCover)
    }
}

