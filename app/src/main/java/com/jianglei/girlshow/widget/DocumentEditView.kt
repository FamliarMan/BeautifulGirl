package com.jianglei.girlshow.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.jianglei.girlshow.R

/**
 * 带有文档提示的编辑框
 */
class DocumentEditView : DocumentView {


    @ColorInt
    private var hintColor: Int = Color.GRAY

    private var hint: String? = null


    private var isRequested: Boolean = false

    private lateinit var evContent: EditText

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun getView(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View {

        val view = LayoutInflater.from(context).inflate(R.layout.widget_document_edit_view, this, false)
        tvTitle = view.findViewById(R.id.tvTitle)
        evContent = view.findViewById(R.id.evContent)
        ivHelp = view.findViewById(R.id.ivHelp)
        evContent.hint = hint
        evContent.setHintTextColor(hintColor)
        evContent.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                setOnlyDataContent(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        initXML(context, attrs)
        evContent.setText(contentValue)
        return view

    }


    override fun setContent(content: String?) {
        super.setContent(content)
        evContent.setText(content)
    }

    fun setOnlyDataContent(content:String?){
        super.setContent(content)
    }
    private fun initXML(context: Context, attrs: AttributeSet?) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DocumentEditView
        )
        hint = a.getString(R.styleable.DocumentEditView_dev_hint)
        hintColor = a.getColor(R.styleable.DocumentEditView_dev_hintColor, Color.GRAY)
        isRequested = a.getBoolean(R.styleable.DocumentEditView_dev_isRequested, false)
        setRequested(isRequested)
        a.recycle()


    }


    fun setHint(hint: String?) {
        this.hint = hint
        evContent.hint = hint
    }

    fun setHintColor(@ColorRes color: Int) {
        this.hintColor = ContextCompat.getColor(context, color)
        evContent.setHintTextColor(hintColor)
    }

    fun setRequested(isRequested: Boolean) {
        this.isRequested = isRequested
        if (isRequested) {
            setHint(context.getString(R.string.edit_required))
            setHintColor(R.color.red)
        } else {
            setHint(context.getString(R.string.edit_choose))
            setHintColor(R.color.grey)
        }
    }

}
