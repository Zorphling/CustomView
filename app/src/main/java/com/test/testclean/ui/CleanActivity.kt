package com.test.testclean.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.test.testclean.R
import kotlinx.android.synthetic.main.activity_clean.*

class CleanActivity : AppCompatActivity() {
    var animator: Animator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clean)

        val fingerRotateAnim =
            ObjectAnimator.ofFloat(iv_finger_copy, View.ROTATION, 0f, 360F).apply {
                duration = 2000 //自旋转时间
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()//插值器统一旋转速度
                addUpdateListener {
                    cleannn.refreshSweep(it.animatedValue as Float)
                }
            }

        stop.setOnClickListener {
//        lifecycle.addObserver(CleanViewLifeObserver(clean_view))//会报错
            fingerRotateAnim.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}