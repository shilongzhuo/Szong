package com.example.szong.data.music

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint


class LyricEntry(val time: Long, val text: String) : Comparable<LyricEntry> {

    companion object {
        const val GRAVITY_CENTER = 0 // 居中
        const val GRAVITY_LEFT = 1 // 左
        const val GRAVITY_RIGHT = 2 // 右
    }

    /**
     * 第二文本
     */
    var secondText: String? = null

    /**
     * 显示的文本
     */
    private val showText: String
        get() = if (secondText.isNullOrEmpty()) {
            text
        } else {
            "$text\n$secondText"
        }

    /**
     * staticLayout
     */
    var staticLayout: StaticLayout? = null
        private set

    /**
     * 歌词距离视图顶部的距离
     */
    var offset = Float.MIN_VALUE

    /**
     * 高度
     * get 获取此句歌词高度
     */
    val height: Int
        get() = staticLayout?.height ?: 0

    /**
     * 初始化
     * @param paint 文本画笔
     * @param width 宽度
     * @param gravity 位置
     */
    fun init(paint: TextPaint, width: Int, gravity: Int) {
        val align: Layout.Alignment = when (gravity) {
            GRAVITY_LEFT -> Layout.Alignment.ALIGN_NORMAL
            GRAVITY_CENTER -> Layout.Alignment.ALIGN_CENTER
            GRAVITY_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }
        staticLayout =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                StaticLayout.Builder
                    .obtain(showText, 0, showText.length, paint, width)
                    .setAlignment(align)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(false)
                    .build()
            } else {
                StaticLayout(showText, paint, width, align, 1f, 0f, false)
            }

        offset = Float.MIN_VALUE
    }

    /**
     * 继承 Comparable 比较
     * @param other LyricEntry
     * @return 时间差
     */
    override fun compareTo(other: LyricEntry): Int {
        return (time - other.time).toInt()
    }

}