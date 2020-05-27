package com.huxq17.example.mzitu

import android.app.Application
import android.graphics.Bitmap
import com.huxq17.example.BuildConfig
import com.squareup.picasso.Picasso
import com.tencent.bugly.crashreport.CrashReport


class App : Application() {
    private val picasso: Picasso by lazy {
        Picasso.Builder(applicationContext)
                .loggingEnabled(false)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Picasso.setSingletonInstance(picasso)
        CrashReport.initCrashReport(applicationContext, "5d27c16bd9", BuildConfig.DEBUG);
    }
}