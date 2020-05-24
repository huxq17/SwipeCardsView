package com.huxq17.example.mzitu.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.andbase.tractor.listener.impl.LoadListenerImpl
import com.andbase.tractor.task.Task
import com.andbase.tractor.task.TaskPool
import com.huxq17.example.R
import com.huxq17.example.base.BaseActivity
import com.huxq17.example.http.HttpSender
import com.huxq17.example.mzitu.bean.PostItem
import kotlinx.android.synthetic.main.activity_gallery.*
import org.jsoup.Jsoup

class GalleryActivity : BaseActivity() {
    companion object {
        fun start(context: Context, item: PostItem) {
            val intent = Intent(context, GalleryActivity::class.java).apply {
                putExtra("item", item)
            }
            context.startActivity(intent)
        }
    }

    private var item: PostItem? = null
    private var adapter: GalleryAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        item = intent.getParcelableExtra("item")
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tvPageIndex.text = "${position + 1} / ${adapter?.count}"
            }

        })
        loadData()
    }

    private fun loadData() {
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                adapter = GalleryAdapter(supportFragmentManager, result as List<GalleryBean>)
                viewPager.adapter = adapter
                tvPageIndex.text = "1 / ${adapter?.count}"
            }
        }
        listener.setDismissTime(0)
        TaskPool.getInstance().execute(object : Task("tag", listener) {
            override fun onRun() {
                val httpResponse = HttpSender.instance().getSync(item?.url, null, null)
                val html = httpResponse.string()
                if (html != null) {
                    val doc = Jsoup.parse(html)
                    val imageElement = doc.select("div.main-image").select("img")
                    val image = imageElement.attr("src")
                    var totalPage = doc.select("div.pagenavi").select("a").let { element ->
                        element[element.size - 2].attr("href").let {
                            it.substring(it.lastIndexOf("/") + 1).toInt()
                        }
                    }
                    val galleryList = arrayListOf<GalleryBean>()
                    galleryList.add(GalleryBean(item?.url ?: "", image,
                            imageElement.attr("width").toInt(),
                            imageElement.attr("height").toInt()))
                    for (index in 2..totalPage) {
                        galleryList += GalleryBean("${item?.url}/$index", null, 0, 0)
                    }

                    notifySuccess(galleryList)
                } else {
                    notifyFail("网络异常");
                }
            }

            override fun cancelTask() {
            }

        })
    }

    class GalleryAdapter(fm: FragmentManager, private val galleryList: List<GalleryBean>) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return galleryList.size
        }

        override fun getItem(position: Int): Fragment {
            return GalleryFragment.create(galleryList[position])
        }
    }
}