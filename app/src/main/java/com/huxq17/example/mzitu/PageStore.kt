package com.huxq17.example.mzitu


object PageStore {
    private val map = hashMapOf<String, Int>()
    fun storePage(url: String?, page: Int) {
        url?.let {
            map[it] = page
        }
    }
    fun clearPage(url:String?){
        url?.let {
            map.remove(it)
        }
    }

    fun restorePage(url: String): Int = map[url] ?: 0
}