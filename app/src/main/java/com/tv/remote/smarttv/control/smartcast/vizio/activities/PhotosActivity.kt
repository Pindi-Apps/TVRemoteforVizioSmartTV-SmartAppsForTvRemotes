package com.tv.remote.smarttv.control.smartcast.vizio.activities

import com.google.android.material.tabs.TabLayout
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.PhotosViewPager
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityPhotosBinding
import com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments.PhotosFolderFragment
import com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments.PhotosFragment

class PhotosActivity : BaseActivity<ActivityPhotosBinding>() {
    override fun getViewBinding(): ActivityPhotosBinding {
        return ActivityPhotosBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        val viewPager = PhotosViewPager(supportFragmentManager)
        viewPager.addFragment(PhotosFragment(),"All")
        viewPager.addFragment(PhotosFolderFragment(),"Albums")
        binding.viewPager.adapter=viewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }

    override fun actions() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}