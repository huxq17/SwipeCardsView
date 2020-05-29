package com.huxq17.example.mzitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andbase.tractor.listener.impl.LoadListenerImpl
import com.andbase.tractor.task.Task
import com.andbase.tractor.task.TaskPool
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadInfo
import com.huxq17.download.core.DownloadListener
import com.huxq17.download.utils.LogUtil
import com.huxq17.example.R
import com.huxq17.example.base.BaseFragment
import com.huxq17.example.bean.TabBean
import com.huxq17.example.http.HttpSender
import com.huxq17.example.mzitu.bean.PostItem
import com.huxq17.example.mzitu.decoration.GridSpacingItemDecoration
import com.huxq17.example.mzitu.gallery.GalleryActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_mzitu.*
import kotlinx.android.synthetic.main.layout_post_item.view.*
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URLEncoder


class MzituFragment : BaseFragment() {
    companion object {
        fun create(tabBean: TabBean) = MzituFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable("data", tabBean)
            arguments = bundle
        }
    }

    private var tabData: TabBean? = null
    private var adapter: MzituAdapter? = null
    private val map: HashMap<String, MeiziViewHolder> = HashMap()
    private var pageIndex: Int = 1
    private var totalPage: Int = 1
    private val downloadListener = object : DownloadListener(this) {

        override fun onSuccess() {
            super.onSuccess()
            val downloadInfo = downloadInfo
            map[downloadInfo.url]?.bindImage(downloadInfo)
        }

    }

    private fun isZhuanTi() = tabData?.isZhuanTi == true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mzitu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadListener.enable()
        tabData = arguments?.getParcelable("data")
        rvMzitu.layoutManager = GridLayoutManager(context, 2)
        rvMzitu.addItemDecoration(GridSpacingItemDecoration(3, dp2px(10), true))
        adapter = MzituAdapter(this)
        rvMzitu.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (adapter?.itemCount ?: 0 > 0) return
        loadData()
    }

    fun search(tabBean: TabBean) {
        this.tabData = tabBean
        pageIndex = 1
        adapter?.clear()
        loadData()
    }

    private fun loadData(isLoadMore: Boolean = false) {
        if (tabData == null) return
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                if (isLoadMore) {
                    adapter?.loadMore(result as List<PostItem>)
                } else {
                    adapter?.setData(result as List<PostItem>)
                }

            }

            override fun onLoading(result: Any?) {
                super.onLoading(result)
                totalPage = result as Int
            }

            override fun onFail(result: Any?) {
                super.onFail(result)
                toast(result as String)
            }
        }
        listener.setDismissTime(0)
        TaskPool.getInstance().execute(object : Task(MzituFragment::class.java.simpleName, listener) {
            override fun onRun() {
                val httpResponse = HttpSender.instance().getSync(getUrl(), null, null)
                val html = httpResponse.string()
                if (html != null) {
                    val doc = Jsoup.parse(html)
                    val postList = if (isZhuanTi()) {
                        parseZhuanTiList(doc)
                    } else {
                        var totalPage = 1
                        doc.select("div.pagination").select("a")?.forEach { element ->
                            val total = element.text().toIntOrNull() ?: 0
                            if (totalPage < total) {
                                totalPage = total
                            }
                        }
                        notifyLoading(totalPage)
                        parseMeiziList(doc)
                    }
                    notifySuccess(postList)
                } else {
                    notifyFail("网络异常");
                }
            }

            override fun cancelTask() {
            }

        })
    }

    private fun parseMeiziList(doc: Document): List<PostItem> {
        val elements = doc.select("div.postlist")
        val postList = arrayListOf<PostItem>()
        elements[0]?.childNode(1)?.childNodes()?.filterIsInstance<Element>()?.forEach {
            val time = it.select("span.time").text() ?: ""
            val image = it.select("img").attr("data-original") ?: ""
            it.select("a")?.let { a ->
                if (a.size > 0) {
                    val title = a[1].text() ?: ""
                    val url = a[1].attr("href") ?: ""
                    postList.add(PostItem(image, url, title, time))
                }
            }
        }
        return postList
    }

    private fun parseZhuanTiList(doc: Document): List<PostItem> {
        val postList = arrayListOf<PostItem>()
        doc.select("dl.tags").select("dd")?.forEach {
            val url = it.select("a")?.attr("href") ?: ""
            val image = it.select("img")?.attr("data-original") ?: ""
            val title = it.select("a")?.text() ?: ""
            val total = it.select("i")?.text() ?: ""
            postList.add(PostItem(image, url, title, total))
        }
        return postList
    }

    private fun loadMore() {
        if (pageIndex >= totalPage) return
        pageIndex++
        loadData(true)
    }

    private fun getUrl() = tabData?.href + if (pageIndex > 1) "page/$pageIndex" else ""

    class MzituAdapter(private val fragment: MzituFragment) : RecyclerView.Adapter<MeiziViewHolder>() {
        private var isLoading = false
        private val list = arrayListOf<PostItem>()
        fun setData(data: List<PostItem>) {
            list.clear()
            list.addAll(data)
            notifyDataSetChanged()
        }

        fun loadMore(data: List<PostItem>) {
            isLoading = false
            val listSize = itemCount
            list.addAll(data)
            notifyItemRangeChanged(listSize, data.size)
        }

        fun clear() {
            list.clear()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MeiziViewHolder(parent, fragment.isZhuanTi())

        override fun getItemCount() = list.size
        override fun onViewDetachedFromWindow(holder: MeiziViewHolder) {
            super.onViewDetachedFromWindow(holder)
            fragment.map.remove(holder.image)
            LogUtil.e("unbind item="+holder.image)
        }
        override fun onBindViewHolder(holder: MeiziViewHolder, position: Int) {
            val item = list[position]
            LogUtil.e("bind item="+item.image)
            holder.loadImage(item.image, fragment.getUrl())
            fragment.map[item.image] = holder
            holder.itemView.tvTitle.text = item.title
            holder.itemView.tvTime.text = item.time
            if (position == itemCount - 1 && !isLoading) {
                isLoading = true
                fragment.loadMore()
            }
            holder.itemView.setOnClickListener {
                if (fragment.isZhuanTi()) {
                    ZhuanTiActivity.start(it.context, item.url)
                } else {
                    GalleryActivity.start(it.context, item)
                }
            }
        }
    }

    class MeiziViewHolder(parent: ViewGroup, private val isZhuanTi: Boolean) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_post_item, parent, false)) {
        var image: String = ""

        init {
            itemView.ivMeiziCover.let {
                it.widthRatio = if (!isZhuanTi) 256 else 1
                it.heightRatio = if (!isZhuanTi) 354 else 1
            }
        }

        fun loadImage(image: String, referer: String) {
            this.image = image
            Pump.newRequest(image)
                    .disableBreakPointDownload()
                    .setDownloadTaskExecutor(App.getInstance().imageDispatcher)
                    .setRequestBuilder(Request.Builder()
                            .addHeader("referer", URLEncoder.encode(referer, "utf-8"))
                    )
                    .submit()
        }

        fun bindImage(downloadInfo: DownloadInfo) {
            if (downloadInfo.url != image) return
            Picasso.get().load(File(downloadInfo.filePath))
                    .fit()
                    .into(itemView.ivMeiziCover)
        }
    }
}