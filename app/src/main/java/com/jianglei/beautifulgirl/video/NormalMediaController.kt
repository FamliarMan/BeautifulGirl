package com.jianglei.beautifulgirl.video

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import com.jianglei.beautifulgirl.R
import io.vov.vitamio.MediaPlayer
import io.vov.vitamio.widget.MediaController

/**
 * @author jianglei on 1/7/19.
 */
class NormalMediaController(context: Context) : MediaController(context) {
    private  lateinit var seekBar:SeekBar
    private lateinit var loading:ProgressBar
    /**
     * 当前是否在拖动进度条
     */
    private var isDragging = false
    override fun makeControllerView(): View {
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.media_controller,null)
        seekBar = layout.findViewById(R.id.seekBar)
        loading = layout.findViewById(R.id.progress)
        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        return layout
    }
}