package fr.ffnet.downloader.fanfiction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FanfictionTabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var fragmentList: List<Pair<String, Fragment>> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Fragment = fragmentList[position].second

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence = fragmentList[position].first
}