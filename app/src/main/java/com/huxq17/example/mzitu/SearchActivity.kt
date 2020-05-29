package com.huxq17.example.mzitu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.huxq17.example.R
import com.huxq17.example.base.BaseActivity
import com.huxq17.example.bean.TabBean
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : BaseActivity() {
    companion object {
        fun start(context: Context) {
            Intent(context, SearchActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.let {
                context.startActivity(it)
            }
        }
    }

    private lateinit var mzituFragment: MzituFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mzituFragment = supportFragmentManager.findFragmentById(R.id.mzituFragment) as MzituFragment
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mzituFragment.search(TabBean("", "https://www.mzitu.com/search/${etSearch.text}/"))
            }
            false
        }
        etSearch.post {
            etSearch.requestFocus()
        }

    }
}