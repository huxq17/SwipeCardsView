package com.huxq17.example.mzitu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.huxq17.example.R
import com.huxq17.example.base.BaseActivity
import com.huxq17.example.bean.TabBean

class ZhuanTiActivity : BaseActivity() {
    companion object {
        fun start(context: Context, url: String) {
            val intent = Intent(context, ZhuanTiActivity::class.java).apply {
                putExtra("url", url)
            }
            context.startActivity(intent)
        }
    }

    private val fragmentTag = ZhuanTiActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zhuanlan)
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                ?: MzituFragment.create(TabBean("", intent.getStringExtra("url") ?: ""))

        supportFragmentManager.beginTransaction().let {
            if (!fragment.isAdded) {
                it.add(R.id.flContainer, fragment, fragmentTag)
            }
            it.show(fragment) .commit()
        }


    }
}