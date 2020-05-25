package com.huxq17.example.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TabBean (val title:String,val href:String,var isZhuanTi:Boolean = false):Parcelable