package com.test.testclean.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import com.test.testclean.R
import com.test.testclean.constant.log
import kotlin.math.min

class CleanView(context: Context, attr: AttributeSet) : View(context) {
    var isAutoSize: Boolean = false
    private val DISTANCE_ONE_TO_TWO = dp2px(50f)
    private val DISTANCE_TWO_TO_THREE = dp2px(10f)
    private val DISTANCE_THREE_TO_FOUR = dp2px(10f)
    private val DISTANCE_FOUR_TO_FIVE = dp2px(15f)
    var RADIUS_CIRCLE_ONE: Int = 0
    var RADIUS_CIRCLE_TWO: Int = 0
    var RADIUS_CIRCLE_THREE: Int = 0
    var RADIUS_CIRCLE_FOUR: Int = 0
    var RADIUS_CIRCLE_FIVE: Int = 0

    private val mStrokePaint: Paint by lazy { Paint() }
    private val mFillPaint: Paint by lazy { Paint() }
    private val mPointPaint: Paint by lazy { Paint() }
    private val WIDTH_CIRCLE_FIVE = dp2px(2f)
    private val circleProgressColor = 0xFFFFFFFF.toInt()
    var oldRadiusOne: Int = 0
    lateinit var mCanvas: Canvas

    //    var mCenterPoint: Point = Point()
    lateinit var mCenterPoint: Point
    val scanAnim by lazy {
        ValueAnimator.ofFloat(0f, 1f).let {
            it.duration = 3000
            it.repeatCount = ValueAnimator.DURATION_INFINITE.toInt()
            it.addUpdateListener {

            }
        }
    }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attr, R.styleable.ScannerView)
        isAutoSize = typedArray.getBoolean(R.styleable.ScannerView_autoSize, false)
        typedArray.recycle()
        initPaint()
    }

    private fun initPaint() {
        mStrokePaint.color = 0xFFFFFFFF.toInt()
        mStrokePaint.isAntiAlias = true
        mStrokePaint.style = Paint.Style.STROKE
        mFillPaint.color = 0xFFFFFFFF.toInt()
        mPointPaint.color = 0xFF01448F.toInt()
        mPointPaint.strokeCap = Paint.Cap.ROUND//drawPoints 说的  用这个 不然是方形
        mPointPaint.isAntiAlias = true //设置抗锯齿可画大一点,最大4px
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        log("onSizeChanged -- $w -- $$h")
        initValue(w, h)

    }

    private fun initValue(w: Int, h: Int) {
        mCenterPoint = Point()
        mCenterPoint.x = w / 2
        mCenterPoint.y = h / 2
        RADIUS_CIRCLE_ONE = min(w, h) / 2 / 2 - dp2px(20f)
        log("initValue -- $RADIUS_CIRCLE_ONE")
        RADIUS_CIRCLE_TWO = RADIUS_CIRCLE_ONE + DISTANCE_ONE_TO_TWO
        log("initValue -- $RADIUS_CIRCLE_TWO")
        RADIUS_CIRCLE_THREE = RADIUS_CIRCLE_TWO + DISTANCE_TWO_TO_THREE
        log("initValue -- $RADIUS_CIRCLE_THREE")
        RADIUS_CIRCLE_FOUR = RADIUS_CIRCLE_THREE + DISTANCE_THREE_TO_FOUR
        log("initValue -- $RADIUS_CIRCLE_FOUR")
        RADIUS_CIRCLE_FIVE = RADIUS_CIRCLE_FOUR + DISTANCE_FOUR_TO_FIVE
        log("initValue -- $RADIUS_CIRCLE_FIVE")
        oldRadiusOne = RADIUS_CIRCLE_ONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isAutoSize) {
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val size = width.coerceAtMost(height)
            setMeasuredDimension((size * 0.8).toInt(), (size * 0.8f).toInt())
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        extendAnim.start()//只能说不会报错，但流程是没对的
        postDelayed({
            val animator = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 360F).apply {
                duration = 2000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
                start()
            }
        }, 2000)
    }

    override fun onDraw(canvas: Canvas?) {
        this.mCanvas = canvas!!
        //最外层弧形
        drawDottedLines()

        // 中心空白区
        drawSolid()

        drawPoints()
    }

    private fun drawPoints() {
//        for (i in 0..360 step 15) {
//
//        }
        var pts : FloatArray = floatArrayOf()
        for ( i in 0..24){
//            pts.set()
        }
        mCanvas.drawPoints(pts,15, 24, mPointPaint)
    }

    /**
     * 中间白色实心圆
     */
    private fun drawSolid() {
        mCanvas.drawCircle(
            mCenterPoint.x.toFloat(),
            mCenterPoint.y.toFloat(),
            RADIUS_CIRCLE_FOUR.toFloat(),
            getSolidPaint(0f, 0xFFFFFFFF.toInt())
        )
    }

    private fun getSolidPaint(strokeWidth: Float, paintColor: Int): Paint {
        return mFillPaint.apply {
            style = Paint.Style.FILL
            color = paintColor
        }
    }

    private fun drawDottedLines() {
        drawDottedLine(-65f, 15f)
        drawDottedLine(15f, 75f)
        drawDottedLine(170f, 15f)
        drawDottedLine(205f, 60f)
    }

    var value: Float = 0f
    private val extendAnim = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1500
        addUpdateListener {
            value = it.animatedValue as Float
            invalidate()
        }
    }

    private fun drawDottedLine(startAngle: Float, sweepAngle: Float) {
        mCanvas.drawArc(
            mCenterPoint.x - RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.y - RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.x + RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.y + RADIUS_CIRCLE_FIVE.toFloat(),
            startAngle,
            sweepAngle * value,
            false,
            getStrokePaint(WIDTH_CIRCLE_FIVE.toFloat(), circleProgressColor)
        )
    }

    private fun getStrokePaint(strokeWidth: Float, @ColorInt color: Int): Paint {
        return getPaint(mStrokePaint, strokeWidth, color)
    }

    private fun getPaint(paint: Paint, mStrokeWidth: Float, @ColorInt mColor: Int): Paint {
        return paint.apply {
            strokeWidth = mStrokeWidth
            color = mColor
        }
    }

    private fun dp2px(dp: Float): Int {
        return (resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    private fun sp2px(sp: Float): Int {
        return (resources.displayMetrics.scaledDensity * sp + 0.5f).toInt()
    }
}