package com.example.szong.util.image

import android.graphics.Bitmap


/**
 * 将图片中心裁切为正方形
 */
fun Bitmap.toSquare(): Bitmap {
    val width = this.width
    val height = this.height
    if (width == height) {
        return this
    }
    val clipWidth = width.coerceAtMost(height)
    val x = (width - clipWidth) / 2
    val y = (height - clipWidth) / 2
    return Bitmap.createBitmap(this, x, y, clipWidth, clipWidth)
}