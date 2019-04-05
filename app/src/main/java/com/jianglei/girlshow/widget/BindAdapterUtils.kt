package com.jianglei.girlshow.widget

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

/**
 *@author longyi created on 19-3-25
 */
class BindAdapterUtils {
    companion object {
        @InverseBindingAdapter(attribute = "dv_content")
        @JvmStatic
        fun getContent(view: DocumentView): String {
            return if (view.contentValue == null) {
                ""
            } else {
                view.contentValue!!
            }

        }

        @InverseBindingAdapter(attribute = "dv_content")
        @JvmStatic
        fun getContentBoolean(view: DocumentView): Boolean {
            return !(view.contentValue == null || view.contentValue == "0")

        }

        @InverseBindingAdapter(attribute = "dv_content")
        @JvmStatic
        fun getContentInt(view: DocumentView): Int {
            if (view.contentValue == null || view.contentValue=="") {
                return 0
            }
            return view.contentValue!!.toInt()

        }

        @BindingAdapter("dv_content")
        @JvmStatic
        fun setContent(view: DocumentView, newValue: String?) {
            if (view.contentValue != newValue) {
                view.setContent(newValue)
            }
        }

        @BindingAdapter("dv_content")
        @JvmStatic
        fun setContentBoolean(view: DocumentView, newValue: Boolean?) {
            val c = if (newValue != null && newValue) {
                "1"
            } else {
                "0"
            }
            if (view.contentValue != c) {
                view.setContent(c)
            }
        }

        @BindingAdapter("dv_content")
        @JvmStatic
        fun setContentInt(view: DocumentView, newValue: Int?) {
            val c = if (newValue == null) {
                ""
            } else {
                newValue.toString()
            }
            if (view.contentValue != c) {
                view.setContent(c)
            }
        }


        @BindingAdapter("dv_contentAttrChanged")
        @JvmStatic
        fun setContentListener(view: DocumentView, contentChange: InverseBindingListener) {
            view.onContentChangeListener = object : DocumentView.OnContentChangeListener {
                override fun onChange(old: String?, new: String?) {
                    contentChange.onChange()
                }
            }
        }
    }
}