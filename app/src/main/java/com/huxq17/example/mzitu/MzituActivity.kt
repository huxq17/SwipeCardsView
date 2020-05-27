package com.huxq17.example.mzitu

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.andbase.tractor.listener.impl.LoadListenerImpl
import com.andbase.tractor.task.Task
import com.andbase.tractor.task.TaskPool
import com.huxq17.download.DownloadProvider.context
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadListener
import com.huxq17.download.core.SimpleDownloadTaskExecutor
import com.huxq17.example.R
import com.huxq17.example.base.BaseActivity
import com.huxq17.example.bean.TabBean
import com.huxq17.example.http.HttpSender
import com.huxq17.example.mzitu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_mzitu.*
import org.jsoup.Jsoup
import java.io.File

class MzituActivity : BaseActivity() {
    private val tabList = listOf(
            TabBean("最新", "https://www.mzitu.com/"),
            TabBean("最热", "https://www.mzitu.com/hot/"),
            TabBean("推荐", "https://www.mzitu.com/best/"),
            TabBean("专题", "https://www.mzitu.com/zhuanti/", true)
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
        fabSearch.setOnClickListener {
            SearchActivity.start(this)
        }
    }

    override fun onStart() {
        super.onStart()
        upgrade()
    }

    private fun upgrade() {
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                AlertDialog.Builder(this@MzituActivity)
                        .setTitle("更新提示")
                        .setMessage("1.优化用户体检\n2.解决若干bug")
                        .setPositiveButton("下载", DialogInterface.OnClickListener { _, _ ->
                            downloadApk(result as String)
                        })

            }
        }
        listener.setDismissTime(0)
        TaskPool.getInstance().execute(object : Task() {
            override fun onRun() {
                val httpResponse = HttpSender.instance().getSync("https://github.com/huxq17/SwipeCardsView/tree/dev/apk", null, null)
                val html = httpResponse.string()
                if (html != null) {
                    Jsoup.parse(html).select("span.css-truncate")?.let { elements ->
                        if (elements.size > 0) {
                            elements[0].text()?.let {
                                val preIndex = it.lastIndexOf("_") + 1
                                val lastDotIndex = it.lastIndexOf(".")
                                val apkVersionCode = it.substring(preIndex, lastDotIndex).toIntOrNull()
                                        ?: 0
                                if (apkVersionCode > AppUtils.getVersionCode(this@MzituActivity)) {

                                    notifySuccess("https://raw.githubusercontent.com/huxq17/SwipeCardsView/master/apk/$it")
                                }
                            }

                        }
                    }

                } else {
                    notifyFail("网络异常");
                }
            }

            override fun cancelTask() {
            }

        })
    }

    private val apkDownloadExecutor = object : SimpleDownloadTaskExecutor() {
        override fun getMaxDownloadNumber() = 1
    }

    private fun downloadApk(apkUrl: String) {
        toast("后台下载中...")
        Pump.newRequest(apkUrl)
                .tag("apk")
                //apk下载任务运行在独立的线程池中，不受其他下载任务干扰
                .setDownloadTaskExecutor(apkDownloadExecutor)
                .listener(object : DownloadListener(this) {
                    override fun onSuccess() {
                        super.onSuccess()
                        installApk(downloadInfo.filePath)
                    }
                })
                .submit()
    }

    private fun installApk(apkPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(apkPath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(this, "$packageName.fileProvider-installApk", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
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