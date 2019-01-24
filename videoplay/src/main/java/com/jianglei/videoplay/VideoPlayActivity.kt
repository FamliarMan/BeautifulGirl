package com.jianglei.videoplay

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.kk.taurus.playerbase.assist.InterEvent
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.receiver.ReceiverGroup
import com.kk.taurus.playerbase.widget.BaseVideoView
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {
    private var playUrl: String? = null
    private var userPause: Boolean = false
    private var isLandscape: Boolean = false
    private lateinit var mReceiverGroup: ReceiverGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_play)
        playUrl = intent.getStringExtra("playUrl")
        if (playUrl == null) {
            return
        }
//        getPlayUrl()
        init()
        play(playUrl!!)
    }


    fun init() {
        updateVideo(false)
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this)
        playContent.setReceiverGroup(mReceiverGroup)
        playContent.setEventHandler(object : OnVideoViewEventHandler() {
            override fun onAssistHandle(assist: BaseVideoView?, eventCode: Int, bundle: Bundle?) {
                super.onAssistHandle(assist, eventCode, bundle)
                when (eventCode) {
                    InterEvent.CODE_REQUEST_PAUSE -> {
                        userPause = true
                    }

                    DataInter.Event.EVENT_CODE_REQUEST_BACK -> {
                        if (isLandscape) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            finish()
                        }
                    }
                    DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN -> {
                        requestedOrientation = if (isLandscape) {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    }
                    DataInter.Event.EVENT_CODE_ERROR_SHOW -> {
                        playContent.stop()
                    }

                }
            }

        })
        playContent.setOnPlayerEventListener { eventCode, _ ->
            when (eventCode) {
                OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                }
            }
        }
    }

    fun play(url: String) {

        val dataSource = com.kk.taurus.playerbase.entity.DataSource(url)
        playContent.setDataSource(dataSource)
        playContent.start()
    }

    private fun updateVideo(landscape: Boolean) {
        val layoutParams = playContent.layoutParams as ViewGroup.MarginLayoutParams
        if (landscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.setMargins(0, 0, 0, 0)
        } else {
            layoutParams.width = DensityUtils.getScreenWidth(this)
            layoutParams.height = layoutParams.width * 3 / 4
        }
        playContent.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()
        val state = playContent.state
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return
        if (playContent.isInPlaybackState) {
            if (!userPause)
                playContent.resume()
        } else {
            playContent.rePlay(0)
        }
    }

    override fun onPause() {
        super.onPause()

        val state = playContent.state
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return
        if (playContent.isInPlaybackState) {
            playContent.pause()
        } else {
            playContent.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playContent.stopPlayback()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if (newConfig == null) {
            return
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true
            supportActionBar?.hide()
            window.decorView.systemUiVisibility = View.INVISIBLE
            updateVideo(true)
        } else {
            isLandscape = false
            supportActionBar?.show()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            updateVideo(false)
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape)
    }

    override fun onBackPressed() {
        if (isLandscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        super.onBackPressed()
    }

}
