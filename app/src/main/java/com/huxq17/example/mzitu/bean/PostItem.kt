package com.huxq17.example.mzitu.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostItem(var image:String="",
                    var url:String ="",
                    var title:String = "",
                    var time:String = ""):Parcelable