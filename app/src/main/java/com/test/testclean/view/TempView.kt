package com.test.testclean.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.test.testclean.constant.dp2px

class TempView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    val mPaint by lazy { Paint() }
    val mPaintColor = Color.parseColor("#FFFFFF")
    lateinit var mCanvas: Canvas
    val mPath: Path by lazy { Path() }

    init {
        mPaint.color = mPaintColor
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dp2px(10f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        mCanvas = canvas!!
        var newPaint = Paint()
        newPaint.set(mPaint)
//        newPaint.pathEffect = CornerPathEffect(dp2px(45f).toFloat())
//        newPaint.pathEffect = DiscretePathEffect(30f,10f)
//        newPaint.pathEffect = PathDashPathEffect()
//        newPaint.pathEffect = SumPathEffect(CornerPathEffect(dp2px(45f).toFloat()),DiscretePathEffect(10f,20f))
        newPaint.pathEffect = ComposePathEffect(CornerPathEffect(dp2px(15f).toFloat()),DiscretePathEffect(5f,20f))
        mPath.moveTo(200f,200f)
        mPath.lineTo(220f,180f)
        mPath.lineTo(240f,200f)
        mPath.lineTo(200f,230f)
        mPath.close()
        mCanvas.drawPath(mPath,newPaint)
//        mCanvas.drawLine(200f, 200f, 500f, 200f, mPaint)
    }
}