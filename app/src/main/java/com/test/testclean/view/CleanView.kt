package com.test.testclean.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleObserver
import com.test.testclean.R
import com.test.testclean.constant.log
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

open class CleanView(context: Context, attr: AttributeSet) : View(context, attr) {
    private var isAutoSize: Boolean = false //自适应屏幕尺寸 宽高最小值的80%
    private val DISTANCE_ONE_TO_TWO = dp2px(50f)
    private val DISTANCE_TWO_TO_THREE = dp2px(10f)
    private val DISTANCE_THREE_TO_FOUR = dp2px(10f)
    private val DISTANCE_FOUR_TO_FIVE = dp2px(15f)
    private var RADIUS_CIRCLE_ONE: Int = 0
    private var RADIUS_CIRCLE_CENTER_CIRCE: Int = 0
    private var RADIUS_CIRCLE_TWO: Int = 0
    private var RADIUS_CIRCLE_THREE: Int = 0
    private var RADIUS_CIRCLE_FOUR: Int = 0
    private var RADIUS_CIRCLE_FIVE: Int = 0

    private lateinit var rectf: RectF
    private val mStrokePaint: Paint by lazy { Paint() } //最外层点线画笔
    private val mFillPaint: Paint by lazy { Paint() }   //中间白色实心圆
    private val mPointPaint: Paint by lazy { Paint() }  //周围白色点画笔
    private val mSweepGradientPaint: Paint by lazy { Paint() }  //画笔

    private val WIDTH_CIRCLE_FIVE = dp2px(2f) //最外层点线宽度
    private val circleProgressColor = 0xFFFFFFFF.toInt() //最外层点线画笔颜色
    private var oldRadiusOne: Int = 0
    private lateinit var mCanvas: Canvas

    lateinit var mCenterPoint: Point //拿到中心点xy位置

    private var extendValue: Float = 0f // 动态刷新最外层点线长度
    private val extendAnim by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            addUpdateListener {
                extendValue = it.animatedValue as Float
                invalidate()
            }
        }
    }
    private val rotateAnim by lazy {
        ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 360F).apply {
            duration = 2000 //自旋转时间
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()//插值器统一旋转速度
            addUpdateListener {
                invalidate()
            }
        }
    }
    private val scanAnim by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.DURATION_INFINITE.toInt()
            addUpdateListener {

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
        mFillPaint.isAntiAlias = true

        mPointPaint.color = 0XFF8DBBD9.toInt()
        mPointPaint.strokeWidth = dp2px(5f).toFloat()
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
        RADIUS_CIRCLE_CENTER_CIRCE = (RADIUS_CIRCLE_ONE / 2) + dp2px(5f)

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

        rectf = RectF(
            mCenterPoint.x - RADIUS_CIRCLE_FOUR.toFloat(),
            mCenterPoint.x - RADIUS_CIRCLE_FOUR.toFloat(),
            mCenterPoint.x + RADIUS_CIRCLE_FOUR.toFloat(),
            mCenterPoint.x + RADIUS_CIRCLE_FOUR.toFloat()
        )
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
        //只能说不会报错，但流程是没对的
        extendAnim.start()
//        startRotateAnim(1000) //旋转是该View整个旋转，这不是我想要的，需要剔出来单独弄个view
    }

    override fun onDraw(canvas: Canvas?) {
        this.mCanvas = canvas!!
        //最外层弧形
        drawDottedLines()
        // 中心空白区
        drawSolid()
        //画四周二十多个小点
//        if (isDrawPointsOnce) {
        drawPoints()
//        }
        //画中心蓝色圆
        drawCenterCircle()
        //绘制扫描渐变
        drawArc()
    }

    private fun drawArc() {
        if (sweepFloat == -1f) return
        mCanvas.save()
        mCanvas.rotate(-90f,mCenterPoint.x.toFloat(),mCenterPoint.y.toFloat())
        val colors = intArrayOf(0x4C0ACFFE.toInt(),0xFF495AFF.toInt())
        val sweepGradient =
            SweepGradient(
                mCenterPoint.x.toFloat(),
                mCenterPoint.y.toFloat(),
                colors,
                floatArrayOf(0.0f, sweepFloat)
            )
//        val matrix = Matrix()
//        matrix.setRotate(-90f,mCenterPoint.x.toFloat(),mCenterPoint.y.toFloat())
//        sweepGradient.setLocalMatrix(matrix)
        mSweepGradientPaint.shader = sweepGradient
        mCanvas.drawArc(rectf, 0f, sweepFloat, true, mSweepGradientPaint)
        mCanvas.restore()
    }

    fun startRotateAnim(delay: Long) {
        postDelayed(rotateRunnable, delay)
    }

    private val rotateRunnable = Runnable {
        rotateAnim.start()
    }
    private var isDrawPointsOnce = true
    private fun drawPoints() {
        isDrawPointsOnce = false
        var pts = mutableListOf<Float>()
        for (i in 0..360 step 15) {
//            val lineStart =
//                getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_ONE, i.toFloat())
            val lineEnd = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_THREE, i.toFloat())
            pts.add(lineEnd.x.toFloat())
            pts.add(lineEnd.y.toFloat())
        }
        mCanvas.drawPoints(pts.toFloatArray(), mPointPaint)
    }

    private fun drawCenterCircle() {
        mCanvas.drawCircle(
            mCenterPoint.x.toFloat(),
            mCenterPoint.y.toFloat(),
            RADIUS_CIRCLE_CENTER_CIRCE.toFloat(),
            getSolidPaint(0f, 0xFF005BEA.toInt())
        )
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

    private fun drawDottedLine(startAngle: Float, sweepAngle: Float) {
        mCanvas.drawArc(
            mCenterPoint.x - RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.y - RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.x + RADIUS_CIRCLE_FIVE.toFloat(),
            mCenterPoint.y + RADIUS_CIRCLE_FIVE.toFloat(),
            startAngle,
            sweepAngle * extendValue,
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

    /**
     * 根据圆心，半径，角度 获取圆上坐标
     *
     * @param center 圆心坐标点
     * @param radius 半径
     * @param angle  角度
     * @return Point 对应圆上坐标
     */
    private fun getCoordinateOfCircleAngle(center: Point, radius: Int, angle: Float): Point {
        val point = Point()
        point.x = (center.x + radius * cos(angle * 3.14 / 180)).toInt()
        point.y = (center.y + radius * sin(angle * 3.14 / 180)).toInt()
        return point
    }


    fun destory() {
        scanAnim.cancel()
        rotateAnim.cancel()
        extendAnim.cancel()
    }

    fun resume() {
        scanAnim.resume()
        rotateAnim.resume()
        extendAnim.resume()
    }

    fun pause() {
        scanAnim.pause()
        rotateAnim.pause()
        extendAnim.pause()
    }

    var sweepFloat = -1f
    fun refreshSweep(float: Float) {
        sweepFloat = float
        invalidate()
    }
}