package com.example.szong.util.app

import android.util.Log
import com.example.szong.util.security.Secure

/**
 * 全局 log
 */
@JvmOverloads
fun loge(msg: String, tag: String = "Default") {
    if (Secure.isDebug()) {
        runOnMainThread {
            Log.e(tag, "【$msg】")
        }
    }
}