package com.jianglei.beautifulgirl

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @author jianglei on 1/3/19.
 */
class ContentFragmentAdapter(fm: FragmentManager, private val fragments: MutableList<Fragment>,
                             private val titles:MutableList<String>) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return if (fragments.size == 0) 0 else fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}