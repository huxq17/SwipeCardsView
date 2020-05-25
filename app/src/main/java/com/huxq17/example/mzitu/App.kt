package com.huxq17.example.mzitu

import android.app.Application
import android.graphics.Bitmap
import com.squareup.picasso.Picasso


class App : Application() {
    private val picasso: Picasso by lazy {
        Picasso.Builder(applicationContext)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Picasso.setSingletonInstance(picasso)

    }
}