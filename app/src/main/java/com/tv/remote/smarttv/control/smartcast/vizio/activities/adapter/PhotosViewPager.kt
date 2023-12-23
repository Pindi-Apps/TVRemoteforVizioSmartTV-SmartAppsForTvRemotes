package com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter

import android.icu.text.CaseMap.Title
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PhotosViewPager(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var fragmentList = ArrayList<Fragment>()
    private var fragmentTitleList = ArrayList<String>()
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
    return fragmentTitleList[position]
    }
fun addFragment(fragment: Fragment,title: String){
    fragmentList.add(fragment)
    fragmentTitleList.add(title)
}
}