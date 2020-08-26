package com.test.testclean.util

import android.app.Activity
import com.test.testclean.constant.App


/**
 * dp转px
 *
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
 * @param pxValue px值
 * @return dp值
 */
fun px2dp(pxValue: Float): Int {
    val scale = App.application.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 获得屏幕宽高
 * @return [0]width [1]height
 */
fun getScreenWH(activity: Activity): IntArray {
    val screenWH = IntArray(2)
    val mDisplay = activity.windowManager.defaultDisplay
    screenWH[0] = mDisplay.width
    screenWH[1] = mDisplay.height
    return screenWH
}