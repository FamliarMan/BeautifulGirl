package com.jianglei.beautifulgirl.widget

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

/**
 *@author longyi created on 19-3-25
 */
class BindAdapterUtils {
    companion object {
        @InverseBindingAdapter(attribute = "content" )
        @JvmStatic
        fun getContent(view: DocumentView): String {
            return if (view.contentValue == null) {
                ""
            } else {
                view.contentValue!!
            }

        }

        @BindingAdapter("content")
        fun setContent(view: DocumentView, newValue: String) {
            if (view.contentValue != newValue) {
                view.contentValue = newValue
            }
        }

        @BindingAdapter("app:contentAttrChanged")
        fun setContentListener(view: DocumentView, contentChange: InverseBindingListener) {
            view.onContentChangeListener = object : DocumentView.OnContentChangeListener {
                override fun onChange(old: String?, new: String?) {
                    contentChange.onChange()
                }
            }
        }
    }
}