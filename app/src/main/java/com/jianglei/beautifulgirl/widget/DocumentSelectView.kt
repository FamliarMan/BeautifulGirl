package com.jianglei.beautifulgirl.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.jianglei.beautifulgirl.R

/**
 * TODO: document your custom view class.
 */
class DocumentSelectView : DocumentView {


    private var allContents: MutableList<String>? = null


    private lateinit var spinner: Spinner

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun getView(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View {

        allContents = mutableListOf()
        initXML(context, attrs)
        val thisView = LayoutInflater.from(context).inflate(R.layout.widget_document_select_view, this, false)

        tvTitle = thisView.findViewById(R.id.tvTitle)
        spinner = thisView.findViewById(R.id.spinner)
        ivHelp = thisView.findViewById(R.id.ivHelp)

        if (!isInEditMode) {
            spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, allContents)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                setOnlyContent(allContents!![0])
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setOnlyContent(allContents!![position])
            }
        }


        return thisView
    }


    private fun initXML(context: Context, attrs: AttributeSet?) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DocumentSelectView
        )
        val chars = a.getTextArray(R.styleable.DocumentSelectView_android_entries)

        if (chars != null) {
            for (str in chars) {
                allContents!!.add(str.toString())
            }
        }
        a.recycle()


    }

    override fun setContent(content: String?) {
        super.setContent(content)
        if (content != null) {
            val index = allContents!!.indexOf(content)
            if (index != -1) {
                spinner.setSelection(index)
            }
        }
    }

    fun setOnlyContent(content:String?){
        super.setContent(content)
    }


}
