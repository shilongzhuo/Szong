package com.example.szong.widget

import android.widget.Toast
import com.example.szong.App
import com.example.szong.util.app.runOnMainThread
import java.lang.ref.WeakReference

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