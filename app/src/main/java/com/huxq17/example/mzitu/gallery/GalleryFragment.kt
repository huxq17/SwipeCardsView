package com.huxq17.example.mzitu.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andbase.tractor.listener.impl.LoadListenerImpl
import com.andbase.tractor.task.Task
import com.andbase.tractor.task.TaskPool
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadListener
import com.huxq17.download.utils.LogUtil
import com.huxq17.example.R
import com.huxq17.example.base.BaseFragment
import com.huxq17.example.http.HttpSender
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_gallery.*
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File

class GalleryFragment : BaseFragment() {
    companion object {
        fun create(galleryBean: GalleryBean) = GalleryFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable("galleryBean", galleryBean)
            arguments = bundle
        }
    }

    private val galleryBean by lazy {
        arguments!!.getParcelable<GalleryBean>("galleryBean")!!
    }
    private var widthRatio = 0
    private var heightRatio = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryBean.image?.let {
            widthRatio = galleryBean.widthRatio
            heightRatio = galleryBean.heightRatio
            downloadImage(it)
        } ?: run {
            loadData()
        }
    }

    private fun loadData() {
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                downloadImage(result as String)
            }
        }
        listener.setDismissTime(0)
        TaskPool.getInstance().execute(object : Task("tag", listener) {
            override fun onRun() {
                val httpResponse = HttpSender.instance().getSync(galleryBean.url, null, null)
                val html = httpResponse.string()
                if (html != null) {
                    val doc = Jsoup.parse(html)
                    doc.select("div.main-image")?.let {
                        val imageElement = it.select("img")
                        widthRatio = imageElement.attr("width").toIntOrNull()?:0
                        heightRatio = imageElement.attr("height").toIntOrNull()?:0
                        val src = imageElement.attr("src")
                        if(src.isNotBlank()){
                            notifySuccess(src)
                        }else{
                            notifyFail("获取图片失败")
                        }

                    }?:run{
                        notifyFail("网络异常");
                    }
                } else {
                    notifyFail("网络异常");
                }
            }

            override fun cancelTask() {
            }

        })
    }

    private fun downloadImage(imageUrl: String) {
        Pump.newRequest(imageUrl)
                .setRequestBuilder(Request.Builder()
                        .addHeader("accept", "image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5")
                        .addHeader("accept-encoding", "gzip, deflate, br")
                        .addHeader("accept-language", "zh-Hans-CN,zh-Hans;q=0.5")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                                " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18363")
                        .addHeader("referer", galleryBean.url))
                .disableBreakPointDownload()
                .listener(object : DownloadListener(this) {
                    override fun onSuccess() {
                        super.onSuccess()
                        ivGallery.setRatio(widthRatio, heightRatio)
                        Picasso.get().load(File(downloadInfo.filePath))
                                .into(ivGallery)
                    }
                }).submit()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(!isVisibleToUser){
            ivGallery?.attacher?.let {
                it.setScale(it.minimumScale, 0f, 0f, true);
            }
        }
    }

}