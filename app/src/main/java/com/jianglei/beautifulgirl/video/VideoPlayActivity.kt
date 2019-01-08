package com.jianglei.beautifulgirl.video

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.jianglei.beautifulgirl.R
import io.vov.vitamio.LibsChecker
import io.vov.vitamio.MediaPlayer
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.MediaController
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //定义全屏参数
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        //获得当前窗体对象
        val window = window
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag)
        //必须写这个，初始化加载库文件
        Vitamio.initialize(this)
        //设置视频解码监听
        if (!LibsChecker.checkVitamioLibs(this)) {
            return
        }

        setContentView(R.layout.activity_video_play)
        init()
        playContent.start()
    }

    fun init(){
        playContent.setVideoURI(Uri.parse("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8"))
        playContent.setMediaController(MediaController(this))
        playContent.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH)
        playContent.requestFocus()
        playContent.setOnPreparedListener{
            it.setPlaybackSpeed(1.0F)
        }
        playContent.setOnInfoListener(onInfoListener)
        playContent.setOnBufferingUpdateListener(onBufferingUpdateListener)
    }

    private val onInfoListener = MediaPlayer.OnInfoListener { mp, what, extra ->
//        when{
//            what == MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
//
//            }
//            what == MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
//
//            }
//            what == MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
//
//            }else {
//        }
//
//        }
//
//
//        }
        true
    }
    private val onBufferingUpdateListener = MediaPlayer.OnBufferingUpdateListener { mp, percent ->
    }

    override fun onDestroy() {
        super.onDestroy()
        playContent.stopPlayback()
    }

}
