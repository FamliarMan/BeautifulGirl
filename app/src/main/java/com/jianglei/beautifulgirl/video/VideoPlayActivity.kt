package com.jianglei.beautifulgirl.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import com.dl7.player.media.IjkPlayerView
import com.jianglei.beautifulgirl.R
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_play)
        init()
    }

    fun init() {
        playContent.init()
            .setVideoPath(Uri.parse("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8"))
            .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)
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
