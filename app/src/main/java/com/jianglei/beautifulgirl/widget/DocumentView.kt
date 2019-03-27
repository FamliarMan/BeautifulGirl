package com.jianglei.beautifulgirl.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.jianglei.beautifulgirl.R
import utils.DialogUtils
import java.lang.IllegalStateException

/**
 *@author longyi created on 19-3-25
 */
abstract class DocumentView : FrameLayout {

    var contentValue: String? = null


    /**
     * 标题
     */
    var title: String? = null
        set(value) {
            tvTitle?.text = value
            field = value
        }

    var helpContent: String? = null
        set(value) {
            if (value == null) {
                ivHelp?.visibility = View.GONE
            } else {
                ivHelp?.visibility = View.VISIBLE
            }
            field = value
        }
    var ivHelp: ImageView? = null
    var tvTitle: TextView? = null

    var onContentChangeListener: OnContentChangeListener? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DocumentView
        )
        title = a.getString(R.styleable.DocumentView_dv_title)
        contentValue = a.getString(R.styleable.DocumentView_dv_content)
        helpContent = a.getString(R.styleable.DocumentView_dv_help)
        a.recycle()
        addView(getView(context, attrs, defStyleAttr))
        if (ivHelp == null || tvTitle == null) {
            throw IllegalStateException("Please init ivHelp and tvTitle")
        }
        tvTitle!!.text = title
        if(helpContent.isNullOrBlank()){
            ivHelp!!.visibility=View.GONE
        }else{
            ivHelp!!.visibility=View.VISIBLE
        }
        ivHelp!!.setOnClickListener {
            DialogUtils.showTipDialog(context, helpContent)
        }
    }

    /**
     * 获取控件真正的view,
     * 子view应该在这里进行tvTitle,ivHelp的初始化
     */
    abstract fun getView(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View

    open fun setContent(content: String?) {
        val old = this.contentValue
        if(old == content){
            return
        }
        this.contentValue = content
        onContentChangeListener?.onChange(old, content)

    }



    open fun getContent(): String? {
        return contentValue
    }


    interface OnContentChangeListener {
        fun onChange(old: String?, new: String?)
    }

}