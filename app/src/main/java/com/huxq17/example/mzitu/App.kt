package com.huxq17.example.mzitu

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.huxq17.download.PumpFactory
import com.huxq17.download.config.DownloadConfig
import com.huxq17.download.core.DownloadTaskExecutor
import com.huxq17.download.core.SimpleDownloadTaskExecutor
import com.huxq17.download.core.service.IDownloadConfigService
import com.huxq17.example.BuildConfig
import com.huxq17.example.mzitu.utils.Utils
import com.squareup.picasso.Picasso
import com.tencent.bugly.crashreport.CrashReport


class App : Application() {
    private val picasso: Picasso by lazy {
        Picasso.Builder(applicationContext)
                .loggingEnabled(false)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build()
    }
    companion object{
        private lateinit var instance: App
        fun getInstance():App {
            return instance
        }
    }

    var imageDispatcher: DownloadTaskExecutor = object : SimpleDownloadTaskExecutor() {
        override fun getMaxDownloadNumber(): Int {
            return PumpFactory.getService(IDownloadConfigService::class.java).maxRunningTaskNumber
        }

        override fun getName(): String {
            return "ImageDownloadDispatcher"
        }

        override fun getTag(): String {
            return "image"
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Picasso.setSingletonInstance(picasso)
        CrashReport.initCrashReport(applicationContext, "5d27c16bd9", BuildConfig.DEBUG);
        DownloadConfig.newBuilder() //Optional,set the maximum number of tasks to run at the same time, default 3.
                .setMaxRunningTaskNum(2) //Optional,set the minimum available storage space size for downloading to avoid insufficient storage space during downloading, default is 4kb.
                .setMinUsableStorageSpace(4 * 1024L)
                .setDownloadConnectionFactory(AuthorizationHeaderConnection.Factory(Utils.getIgnoreCertificateOkHttpClient())) //Optional
                .build()
    }
}