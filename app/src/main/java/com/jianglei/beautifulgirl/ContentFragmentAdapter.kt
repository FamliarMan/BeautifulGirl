package com.jianglei.beautifulgirl

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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