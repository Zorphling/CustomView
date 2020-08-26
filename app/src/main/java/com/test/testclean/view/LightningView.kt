package com.test.testclean.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.test.testclean.R

class LightningView(context: Context?, attrs: AttributeSet?) : View(context, attrs), LifecycleObserver {

    private val paint = Paint()
    private var bitmap: Bitmap
    private var srcRect: Rect
    private lateinit var dstRectF: RectF

    private lateinit var center: PointF
    private val animator = ValueAnimator.ofInt(0, 23)
    private var leader = 0
    private val step = 15
    private val degreesEnd = 360 - step

    init {
        paint.apply {
            color = Color.parseColor("#FF0000")
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            isFilterBitmap = true
        }

        //图片高质量
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_lightning, options)

        srcRect = Rect(0, 0, bitmap.width, bitmap.height)
        animator.apply {
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                leader = it.animatedValue as Int
                invalidate()
            }
        }
        post { animator.start() }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            canvas.rotate(leader * step.toFloat(), center.x, center.y)
            for (degrees in 0..degreesEnd step step) {
                canvas.save()
                canvas.rotate(degrees.toFloat(), center.x, center.y)
                canvas.translate(center.x, dstRectF.height() + dp2px(4))
                drawContent(canvas, degrees)
                canvas.restore()
            }
        }
    }

    private fun drawContent(canvas: Canvas, degrees: Int) {
        val alpha = (degrees.toFloat() / degreesEnd * 255).toInt()
        canvas.drawBitmap(bitmap, srcRect, dstRectF, getPaint(alpha))
    }

    private fun getPaint(alpha: Int): Paint {
        paint.alpha = alpha
        return paint
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initValues(w, h)
    }

    private fun initValues(w: Int, h: Int) {
        center = PointF(w.toFloat() / 2, h.toFloat() / 2)
        // 10*30 的矩形，以(center.x, 30 + extra)为中心点
        val left = -dp2px(5)
        val right = -left
        val top = -dp2px(31)
        val bottom = 0f
        dstRectF = RectF(left, top, right, bottom)
    }

    private fun dp2px(dp: Int) = resources.displayMetrics.density * dp

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        animator.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        animator.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        animator.cancel()
    }
}