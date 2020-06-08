package com.huxq17.example.mzitu.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.andbase.tractor.listener.impl.LoadListenerImpl
import com.andbase.tractor.task.Task
import com.andbase.tractor.task.TaskPool
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadListener
import com.huxq17.example.R
import com.huxq17.example.base.BaseFragment
import com.huxq17.example.http.HttpSender
import com.huxq17.example.mzitu.App
import com.huxq17.example.mzitu.RxPump
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_gallery.*
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File
import java.net.URLEncoder

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    private var imageUrl: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        galleryBean.image?.let {
            downloadImage(it)
        } ?: run {
            (activity as? GalleryActivity)?.templateImageUrl?.format(galleryBean.page)?.let {
                downloadImage(it)
            }?:run{
                loadData()
            }
        }
        ivGallery.setOnClickListener {
            (activity as? GalleryActivity)?.next()
        }
        ivGallery.setOnLongClickListener {
            imageUrl?.let {
                RxPump.getFileIfSucceed(it).subscribe { file: File? ->
                    file?.let { it ->
                        share(it)
                    }
                }
            } ?: run {
                false
            }
            true
        }
    }

    private fun loadData() {
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                downloadImage(result as String)
            }

            override fun onFail(result: Any?) {
                super.onFail(result)
                toast(result as String)
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
                        val src = imageElement.attr("src")
                        if (src.isNotBlank()) {
                            notifySuccess(src)
                        } else {
                            notifyFail("获取图片失败")
                        }
                    } ?: run {
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

    private fun share(file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val contentUri = FileProvider.getUriForFile(context!!, "${context!!.packageName}.fileProvider-installApk", file)
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.type = "image/*"
        } else {
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(Intent.createChooser(intent, "分享到"))
    }

    @SuppressLint("CheckResult")
    private fun downloadImage(imageUrl: String) {
        this.imageUrl = imageUrl
        RxPump.getFileIfSucceed(imageUrl).subscribe({ file ->
            loadImage(file)
        }, {
            Pump.newRequest(imageUrl)
                    .setRequestBuilder(Request.Builder()
                            .addHeader("referer", URLEncoder.encode(galleryBean.url, "utf-8"))
                    ).setDownloadTaskExecutor(App.getInstance().imageDispatcher)
                    .threadNum(1)
                    .setRetry(3, 300)
                    .listener(object : DownloadListener(this) {
                        override fun onSuccess() {
                            super.onSuccess()
                            loadImage(File(downloadInfo.filePath))
                        }

                        override fun onFailed() {
                            super.onFailed()
                            toast("服务器异常")
                        }
                    }).submit()
        })

    }

    private fun loadImage(file: File) {
        ivGallery?.let {
            Picasso.get().load(file)
                    .config(Bitmap.Config.ARGB_8888)
                    .into(it)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            ivGallery?.attacher?.let {
                it.setScale(it.minimumScale, 0f, 0f, true);
            }
        }
    }

}