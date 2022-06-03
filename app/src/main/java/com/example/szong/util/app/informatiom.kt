package com.example.szong.util.app

import android.content.Context
import android.graphics.Typeface
import com.example.szong.BuildConfig

/**
 * 获取版本号
 */
fun getVisionCode(): Int {
    return BuildConfig.VERSION_CODE
}

/**
 * 获取版本名
 */
fun getVisionName(): String {
    return BuildConfig.VERSION_NAME
}

fun defaultTypeface(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "fonts/Moriafly-Regular.ttf")
}