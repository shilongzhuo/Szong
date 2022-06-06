package com.example.szong.util.net

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.szong.widget.toast

/**
 * 通过浏览器打开网页
 * @param context
 * @url 网址
 */
fun openUrlByBrowser(context: Context, url: String) {
    if (url != "") {
        try {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(url)
            intent.data = contentUrl
            ContextCompat.startActivity(context, intent, Bundle())
        } catch (e: Exception) {
            toast("启动外部浏览器失败，请点击更新详情链接手动更新~")
        }
    }
}