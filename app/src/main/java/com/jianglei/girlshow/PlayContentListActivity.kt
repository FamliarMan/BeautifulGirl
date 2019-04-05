package com.jianglei.girlshow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.jianglei.girlshow.vo.PlayContent
import com.jianglei.videoplay.VideoPlayActivity
import kotlinx.android.synthetic.main.layout_recyclerview.*
import utils.JsonUtils

class PlayContentListActivity : AppCompatActivity() {

    var playContents: List<PlayContent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recyclerview)
        val playContentStr = intent.getStringExtra("playContents")
        playContents = JsonUtils.parseJsonArrayWithGson(playContentStr, PlayContent::class.java)
        rvContent.pullRefreshEnable = false
        rvContent.setLinearLayout()
        val adapter = object : CommonRecyclerAdapter<PlayContent>(
            this,
            R.layout.listitem_play_content, playContents
        ) {
            override fun onUpdate(helper: BaseAdapterHelper?, item: PlayContent?, position: Int) {
                helper?.setText(R.id.tv_name, item?.title)
                helper?.setText(R.id.tv_desc, item?.desc)
                if (item?.image == "") {
                    helper?.getView<SimpleDraweeView>(R.id.iv_cover)?.visibility = View.GONE
                } else {

                    helper?.getView<SimpleDraweeView>(R.id.iv_cover)
                        ?.setImageURI(item?.image)
                }
            }

        }
        adapter.setOnItemClickListener { viewHolder, view, position ->
            val playContent = playContents!![position]
            val playUrls = playContent.file
            var playUrl: String? = null
            playUrls!!.forEach {
                if (it.defaultQuality) {
                    playUrl = it.videoUrl
                }
            }
            val intent = Intent(this@PlayContentListActivity, VideoPlayActivity::class.java)
            intent.putExtra("playUrl", playUrl)
            startActivity(intent)
        }
        rvContent.setAdapter(adapter)

    }
}
