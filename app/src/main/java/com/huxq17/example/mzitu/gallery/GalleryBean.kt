package com.huxq17.example.mzitu.gallery

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryBean(val url:String, var image:String?=null,val page:String, var widthRatio:Int, var heightRatio:Int):Parcelable