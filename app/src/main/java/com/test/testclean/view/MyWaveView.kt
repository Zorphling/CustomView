package com.test.testclean.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class MyWaveView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet) {

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

}