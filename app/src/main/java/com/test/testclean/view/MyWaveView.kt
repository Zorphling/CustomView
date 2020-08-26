package com.test.testclean.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.test.testclean.constant.log
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.min
import kotlin.time.days

class MyWaveView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mWavePaint by lazy { Paint() }
    private val mWavePaintColor: Int = 0XFFE136F2.toInt()
    lateinit var mCanvas: Canvas
    private val mWavePath: Path by lazy { Path() }
    private var mWaveHeight: Float = 0f
    private var mWaveWidth: Float = 0f
    private var defaultSize: Float = dp2px(200f).toFloat()
    private var maxHeight: Float = 0f
    private var waveNum = 0;

    init {
        mWavePaint.color = mWavePaintColor
        mWavePaint.isAntiAlias = true
        mWaveHeight = dp2px(10f).toFloat()
        mWaveWidth = dp2px(20f).toFloat()
        maxHeight = dp2px(250f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var minSize = min(
            measureSize(defaultSize.toInt(), widthMeasureSpec),
            measureSize(defaultSize.toInt(), heightMeasureSpec)
        )
        setMeasuredDimension(minSize, minSize)
        waveNum = ceil(minSize / mWaveWidth / 2).toInt()

    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        return View.MeasureSpec.getMode(measureSpec).let {
            var size: Int = defaultSize
            when (it) {
                View.MeasureSpec.EXACTLY -> {
                    size = MeasureSpec.getSize(measureSpec)
                }
                View.MeasureSpec.AT_MOST -> {
                    size = min(size, MeasureSpec.getSize(measureSpec))
                }
                else -> {
                }
            }
            size
        }
    }

    override fun onDraw(canvas: Canvas?) {
        mCanvas = canvas!!

        mCanvas.drawPath(getWavePath(), mWavePaint)
    }

    private fun getWavePath(): Path {
//        mWavePath.reset()
        mWavePath.moveTo(defaultSize, maxHeight - defaultSize)
        mWavePath.lineTo(defaultSize, defaultSize)
        mWavePath.lineTo(0f, defaultSize)
        mWavePath.lineTo(0f, maxHeight - defaultSize)

        for (i in 0..4) {
            mWavePath.rQuadTo(mWaveWidth / 2.toFloat(), mWaveHeight, mWaveWidth, 0f)
            mWavePath.rQuadTo(mWaveWidth / 2.toFloat(), -mWaveHeight, mWaveWidth, 0f)
        }
        mWavePath.close()
        return mWavePath
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    private fun px2dp(pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}