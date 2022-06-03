package com.example.szong.util.ui.opration

import android.content.Context
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