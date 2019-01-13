package com.jianglei.beautifulgirl.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.dl7.player.media.IjkPlayerView
import com.jianglei.beautifulgirl.BaseActivity
import com.jianglei.beautifulgirl.R
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.vo.PlayUrl
import kotlinx.android.synthetic.main.activity_video_play.*
import utils.ToastUtils

class VideoPlayActivity : BaseActivity() {
    private var dataSource: DataSource? = null
    private var dataSourceKey: String? = null
    private var detailUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_play)
        detailUrl = intent.getStringExtra("detailUrl")
        dataSourceKey = intent.getStringExtra("dataSourceKey")
        if (dataSourceKey == null || detailUrl == null) {
            ToastUtils.showMsg(this, "Wrong action")
            return
        }
        dataSource = DataSourceCenter.getDataSource(dataSourceKey!!)
        showProgress(true)
        getPlayUrl()
        init()

        retry.setOnClickListener {
            retry.visibility = View.GONE
            getPlayUrl()

        }
    }

    fun getPlayUrl(){
        dataSource!!.fetchVideoUrls(detailUrl!!, object : OnDataResultListener<MutableList<PlayUrl>> {
            override fun onSuccess(data: MutableList<PlayUrl>) {
                showProgress(false)
                data.forEach{
                    if (it.defaultQuality){
                        play(it.videoUrl)
                    }
                }
            }

            override fun onError(msg: String) {
                showProgress(false)
                ToastUtils.showMsg(this@VideoPlayActivity, "获取视频地址失败")
                retry.visibility = View.VISIBLE
            }
        })

    }
    fun init() {
        playContent.init()
            .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)

    }

    fun play(url: String) {
        playContent.setVideoPath(Uri.parse(url))
            .start()
    }

    override fun onResume() {
        super.onResume()
        playContent.onResume()
    }

    override fun onPause() {
        super.onPause()
        playContent.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playContent.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        playContent.configurationChanged(newConfig)
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ) {//横屏
////            val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
////            val window = window
////            window.setFlags(flag, flag)
//            supportActionBar!!.hide()
//        }else if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
//            supportActionBar!!.show()
//        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (playContent.handleVolumeKey(keyCode)) {
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 检测屏幕的方向：纵向或横向
            if (this.resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE
            ) {
                //当前为横屏，切换至竖屏
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

            } else if (this.resources.configuration.orientation
                == Configuration.ORIENTATION_PORTRAIT
            ) {
                //当前为竖屏，按退出键后就结束当前activity
                finish()
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (playContent.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

}
