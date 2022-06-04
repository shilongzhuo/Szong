package com.example.szong.util.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 转换成大小格式
 */
fun Long.toSizeFormat(): String {
    val source = this.toDouble()
    if (this < 1_000) {
        return "${String.format("%.2f", source)} KB"
    }
    if (this < 1_000_000) {
        return "${String.format("%.2f", source / 1_000)} KB"
    }
    if (this < 1_000_000_000) {
        return "${String.format("%.2f", source / 1_000_000)} MB"
    }
    return "${String.format("%.2f", source / 1_000_000_000)} GB"
}

/**
 * 转换成时间格式
 */
fun Long.toTimeFormat(): String {
    // 时，分，秒
    val hour = this / (60 * 60 * 1000)
    val min = this % (60 * 60 * 1000) / (60 * 1000)
    val sec = this % (60 * 1000) / 1000
    return if (hour == 0L) {
        String.format("%02d:%02d", min, sec)
    } else {
        String.format("%02d:%02d:%02d", hour, min, sec)
    }
}

/**
 * 转换成日期时间格式
 */
@SuppressLint("SimpleDateFormat")
fun Long.toDateTimeFormat(): String {
    val date = Date().apply {
        time = this@toDateTimeFormat
    }
    return SimpleDateFormat("yyyy 年 MM 月 dd 日 HH:mm:ss").format(date)
}

/**
 * 转成比特率格式
 */
fun Long.toBitrateFormat(): String {
    return "${this / 1000} kbps"
}