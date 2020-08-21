package com.test.testclean.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class CircleView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet){
    init {

    }
    lateinit var mCanvas: Canvas
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvas = canvas!!
//        mCanvas.drawCircle()
    }
}