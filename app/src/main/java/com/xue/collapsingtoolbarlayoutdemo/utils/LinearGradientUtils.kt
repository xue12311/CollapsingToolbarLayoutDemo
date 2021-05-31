package com.xue.collapsingtoolbarlayoutdemo.utils

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * 线性渐变颜色
 */
class LinearGradientUtils(@ColorInt var mStartColor: Int, @ColorInt var mEndColor: Int) {
    fun setStartColor(startColor: Int) {
        this.mStartColor = startColor
    }
    fun setEndColor(endColor: Int) {
        this.mEndColor = endColor
    }

    //获取某一个百分比间的颜色,radio取值[0,1]
    fun getColor(radio: Float): Int {
        var mRadio=radio
        if (radio > 1) {
            mRadio = 1f
        } else if (radio < 0) {
            mRadio = 0f
        }
        val redStart: Int = Color.red(mStartColor)
        val blueStart: Int = Color.blue(mStartColor)
        val greenStart: Int = Color.green(mStartColor)
        val redEnd: Int = Color.red(mEndColor)
        val blueEnd: Int = Color.blue(mEndColor)
        val greenEnd: Int = Color.green(mEndColor)
        val red = (redStart + ((redEnd - redStart) * mRadio + 0.5)).toInt()
        val greed = (greenStart + ((greenEnd - greenStart) * mRadio + 0.5)).toInt()
        val blue = (blueStart + ((blueEnd - blueStart) * mRadio + 0.5)).toInt()
        return Color.argb(255, red, greed, blue)
    }
}