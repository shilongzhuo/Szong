package com.example.szong.util.ui.opration

import com.example.szong.App

/**
 * dp 转 px
 */
fun dp2px(dp: Float): Float = dp * App.context.resources.displayMetrics.density