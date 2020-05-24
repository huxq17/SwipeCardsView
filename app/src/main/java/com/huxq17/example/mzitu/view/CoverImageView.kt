package com.huxq17.example.mzitu.view

import android.content.Context
import android.util.AttributeSet
import android.view.View


class CoverImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr){
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        val width = View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0)
        val height = width*354/256
        setMeasuredDimension(width,height)
    }
}