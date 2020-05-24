package com.huxq17.example.mzitu.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.github.chrisbanes.photoview.PhotoView


class GalleryImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {
    private var widthRatio: Int = 0
    private var heightRatio: Int = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(widthRatio==0||heightRatio==0){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec)
            return
        }
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        var width = 0
        if (widthRatio > heightRatio) {
            width = View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0)
            height = width * heightRatio / widthRatio
        } else {
            height = View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
            width = height * widthRatio / heightRatio
        }

        setMeasuredDimension(width, height)
    }


    fun setRatio(widthRatio: Int, heightRatio: Int) {
        this.widthRatio = widthRatio
        this.heightRatio = heightRatio
    }
}