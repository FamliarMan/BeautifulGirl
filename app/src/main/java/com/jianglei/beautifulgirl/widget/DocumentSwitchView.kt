package com.jianglei.beautifulgirl.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import com.jianglei.beautifulgirl.R

/**
 * TODO: document your custom view class.
 */
class DocumentSwitchView : DocumentView {

    private lateinit var switchBtn: Switch

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun getView(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_document_switch_view, this, false)
        tvTitle = view.findViewById(R.id.tvTitle)
        switchBtn = view.findViewById(R.id.switchBtn)
        ivHelp = view.findViewById(R.id.ivHelp)
        switchBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                contentValue = "1"
            } else {
                contentValue = "0"
            }
        }
        return view
    }

    override fun setContent(content: String?) {
        super.setContent(content)
        if (content != null && content == "1") {
            switchBtn.textOn
        } else {
            switchBtn.textOff
        }
    }


}
