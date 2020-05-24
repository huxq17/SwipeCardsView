package com.huxq17.example.mzitu

import android.R.attr.spacing
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
import org.jsoup.nodes.Element
import java.io.File


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
    private val map: HashMap<String, ImageViewHolder> = HashMap()
    private var pageIndex: Int = 1
    private val downloadListener = object : DownloadListener(this) {

        override fun onSuccess() {
            super.onSuccess()
            val downloadInfo = downloadInfo
            map[downloadInfo.url]?.bindImage(downloadInfo)
        }

    }

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
        if (adapter?.itemCount?:0 > 0) return
        loadData()
    }

    private fun loadData(isLoadMore: Boolean = false) {
        val listener: LoadListenerImpl = object : LoadListenerImpl() {
            override fun onSuccess(result: Any) {
                super.onSuccess(result)
                if (isLoadMore) {
                    adapter?.loadMore(result as List<PostItem>)
                } else {
                    adapter?.setData(result as List<PostItem>)
                }

            }
        }
        listener.setDismissTime(0)
        TaskPool.getInstance().execute(object : Task(tag, listener) {
            override fun onRun() {
                val httpResponse = HttpSender.instance().getSync(getUrl(), null, null)
                val html = httpResponse.string()
                if (html != null) {
                    val doc = Jsoup.parse(html)
                    val elements = doc.select("div.postlist")
                    val postList = arrayListOf<PostItem>()
                    elements[0]?.childNode(1)?.childNodes()?.filterIsInstance<Element>()?. forEach {
                            val time = it.select("span.time").text()
                            val image = it.select("img").attr("data-original")
                            val title = it.select("a")[1].text()
                            val url = it.select("a")[1].attr("href")
                            postList.add(PostItem(image, url, title, time))
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

    private fun loadMore() {
        pageIndex++
        loadData(true)
    }

    private fun getUrl() = tabData?.href + if (pageIndex > 1) "page/$pageIndex" else ""

    class MzituAdapter(private val fragment: MzituFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var isLoading = false
        private val list = arrayListOf<PostItem>()
        fun setData(data: List<PostItem>) {
            list.clear()
            list.addAll(data)
            notifyDataSetChanged()
        }

        fun loadMore(data: List<PostItem>) {
            isLoading = false
            list.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageViewHolder(parent)

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            Pump.newRequest(item.image)
                    .disableBreakPointDownload()
                    .setRequestBuilder(Request.Builder()
                            .addHeader("accept", "image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5")
                            .addHeader("accept-encoding", "gzip, deflate, br")
                            .addHeader("accept-language", "zh-Hans-CN,zh-Hans;q=0.5")
                            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18363")
                            .addHeader("referer", fragment.getUrl())
                    ).submit()
            fragment.map[item.image] = holder as ImageViewHolder
            holder.itemView.tvTitle.text = item.title
            holder.itemView.tvTime.text = item.time
            if (position == itemCount - 1 && !isLoading) {
                isLoading = true
                fragment.loadMore()
            }
            holder.itemView.setOnClickListener {
                GalleryActivity.start(it.context, item)
            }
        }
    }

    class ImageViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_post_item, parent, false)) {

        fun bindImage(downloadInfo: DownloadInfo) {
            Picasso.get().load(File(downloadInfo.filePath))
                    .fit()
                    .into(itemView.ivPost)
        }
    }

}