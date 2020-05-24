package com.huxq17.example.mzitu

import android.app.Application
import com.squareup.picasso.Picasso

class App : Application() {
    private val picasso: Picasso by lazy {
        Picasso.Builder(applicationContext)
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Picasso.setSingletonInstance(picasso)
    }
}