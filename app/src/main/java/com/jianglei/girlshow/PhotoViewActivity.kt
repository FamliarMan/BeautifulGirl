package com.jianglei.girlshow

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_photo_view.*
import java.util.*


class PhotoViewActivity : BaseActivity() {

    private var urls = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        toolBar.visibility = View.GONE
        urls = intent.getStringArrayListExtra("urls").toMutableList()
//        photoView.setPhotoUri(Uri.parse(urls[0]))
        val pos = intent.getIntExtra("pos", 0)
        bannerPager.adapter = PicAdapter(urls, this)
        bannerPager.currentItem = pos
        bannerPager.pageMargin = 0
        bottomTitle.setText("${pos + 1}/${urls.size}")
        bannerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bottomTitle.setText("${position + 1}/${urls.size}")
            }
        })
    }

    class PicAdapter(val urls: List<String>?, val context: Context) : PagerAdapter() {
        private val recycledViews = LinkedList<View>()
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return if (urls == null) {
                0
            } else {
                urls.size
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var imageView: SimpleDraweeView? = null
            if (recycledViews.size > 0) {
                imageView = recycledViews.first as SimpleDraweeView
                recycledViews.remove(imageView)
            } else {
                imageView = SimpleDraweeView(context)
            }
            var param = imageView.layoutParams
            if (param == null) {
                param = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            } else {
                param.height = ViewGroup.LayoutParams.MATCH_PARENT
                param.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            container.addView(imageView, param)
            imageView.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
            imageView.setImageURI(urls!![position])
            return imageView
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            recycledViews.add(`object`)
        }
    }
}
