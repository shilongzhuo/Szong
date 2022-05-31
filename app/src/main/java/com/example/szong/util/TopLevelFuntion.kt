package com.example.szong.util

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.szong.App
import com.example.szong.BuildConfig
import com.example.szong.util.theme.StatusbarColorUtils
import java.lang.ref.WeakReference


/**
 * 设置状态栏图标颜色
 * @param dark true 为黑色，false 为白色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

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

/**
 * 运行在主线程，更新 UI
 */
fun runOnMainThread(runnable: Runnable) {
    Handler(Looper.getMainLooper()).post(runnable)
}

var sToastRef: WeakReference<Toast>? = null

/**
 * 全局 toast
 */
fun toast(msg: String) {
    runOnMainThread {
        sToastRef?.get()?.cancel()
        val toast = Toast.makeText(App.context, msg, Toast.LENGTH_SHORT)
        toast.show()
        sToastRef = WeakReference(toast)
    }
}

/**
 * dp 转 px
 */
fun dp2px(dp: Float): Float = dp * App.context.resources.displayMetrics.density