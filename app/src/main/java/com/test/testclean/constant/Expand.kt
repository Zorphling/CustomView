package com.test.testclean.constant

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.logging.Logger

fun <T> View.startA(context: Context, clazz: Class<T>) {
    this.setOnClickListener {
        context.startActivity(Intent(context, clazz))
    }
}

const val TAG = "CONSTANT"
fun Any.log(msg: Any) {
    Log.e("$TAG", "$msg")
}

/**
 * dp转px
 *
 * @param context 上下文
 * @param dpValue dp值
 * @return px值
 */
fun dp2px(dpValue: Float): Int {
    val scale = App.application.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * px转dp
 *
 * @param context 上下文
 * @param pxValue px值
 * @return dp值
 */
fun px2dp(pxValue: Float): Int {
    val scale = App.application.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}