package com.test.testclean.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import com.test.testclean.R
import com.test.testclean.constant.log
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.min
import kotlin.math.sin

class RandomDrawView(context: Context, attr: AttributeSet) : View(context) {
    var isAutoSize = false
    lateinit var mCanvers: Canvas
    private lateinit var mPaint: Paint
    private lateinit var mCenterPoint: Point
    var RADIUS_CIRCLE_ONE: Int = 0
    var RADIUS_CIRCLE_TWO: Int = 0

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attr, R.styleable.ScannerView)
        isAutoSize = typedArray.getBoolean(R.styleable.ScannerView_autoSize, false)
        typedArray.recycle()
        initPaint()
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCenterPoint = Point()
        mCenterPoint.x = w / 2
        mCenterPoint.y = h / 2
        RADIUS_CIRCLE_ONE = min(w, h) / 2 / 2 - dp2px(20f)
        RADIUS_CIRCLE_TWO = RADIUS_CIRCLE_ONE + dp2px(10f)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        this.mCanvers = canvas!!
        mCanvers.drawPoint(50f, 50f, mPaint)
        //绘制一组点，坐标位置由float数组指定
        mCanvers.drawPoints(
            floatArrayOf(50f, 100f, 100f, 100f, 150f, 100f, 200f, 100f, 250f, 100f),
            mPaint
        )
        //绘制一组点中指定的几个坐标
        mCanvers.drawPoints(
            floatArrayOf(50f, 150f, 100f, 150f, 150f, 150f, 200f, 150f, 250f, 150f),
            1,
            3,
            mPaint
        )

        var pts = mutableListOf<Float>()
        for (i in 0..360 step 15) {
//            val lineStart =
//                getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_ONE, i.toFloat())
            val lineEnd = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_TWO, i.toFloat())
            pts.add(lineEnd.x.toFloat())
            pts.add(lineEnd.y.toFloat())
        }
        mCanvers.drawPoints(pts.toFloatArray(), mPaint)
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint.color = Color.WHITE
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND;
        mPaint.strokeWidth = 10f
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

    private fun dp2px(dp: Float): Int {
        return (resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    private fun sp2px(sp: Float): Int {
        return (resources.displayMetrics.scaledDensity * sp + 0.5f).toInt()
    }
}