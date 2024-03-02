package com.dtu.deltecdtu.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HolidayViewPagerAdapter(container: FragmentActivity, private val fragmentList: List<Any>) :
    FragmentStateAdapter(container) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]
        return fragment as Fragment
    }
}