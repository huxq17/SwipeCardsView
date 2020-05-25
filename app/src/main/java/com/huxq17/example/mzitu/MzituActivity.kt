package com.huxq17.example.mzitu

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.huxq17.example.R
import com.huxq17.example.base.BaseActivity
import com.huxq17.example.base.BaseFragment
import com.huxq17.example.bean.TabBean
import kotlinx.android.synthetic.main.activity_mzitu.*

class MzituActivity : BaseActivity() {
    private val tabList = listOf(
            TabBean("最新", "https://www.mzitu.com/"),
            TabBean("最热", "https://www.mzitu.com/hot/"),
            TabBean("推荐", "https://www.mzitu.com/best"),
            TabBean("专题", "https://www.mzitu.com/zhuanti/",true)
    )
    private val adapter by lazy {
        TabAdapter(supportFragmentManager, tabList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mzitu)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = tabList.size
        tabLayout.setupWithViewPager(viewPager)
    }

    class TabAdapter(fm: FragmentManager, private val tabList: List<TabBean>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int {
            return tabList.size
        }

        override fun getItem(position: Int): Fragment {
            return MzituFragment.create(tabList[position])
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabList[position].title
        }
    }
}