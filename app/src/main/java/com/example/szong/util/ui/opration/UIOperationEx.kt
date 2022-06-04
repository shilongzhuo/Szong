package com.example.szong.util.ui.opration

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.util.ui.opration.dp2px
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter


/**
 * 拓展函数
 */
/**
 * dp
 */
fun Int.dp(): Int {
    return dp2px(this.toFloat()).toInt()
}
fun Int.asColor(context: Context) = ContextCompat.getColor(context, this)

fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context, this)


fun Int.colorAlpha(alpha: Float): Int {
    val a = if (alpha in 0f..1f) {
        Color.alpha(this) * alpha
    } else {
        255
    }.toInt()
    return Color.argb(a, Color.red(this), Color.green(this), Color.blue(this))
}

fun Int.colorMix(vararg colors: Int): Int {
    var red = Color.red(this)
    var green = Color.green(this)
    var blue = Color.blue(this)
    colors.forEach {
        red += Color.red(it)
        green += Color.green(it)
        blue += Color.blue(it)
    }
    red /= (colors.size + 1)
    green /= (colors.size + 1)
    blue /= (colors.size + 1)
    return Color.rgb(red, green, blue)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}