package com.example.szong.util.app

import android.os.Handler
import android.os.Looper

/**
 * 运行在主线程，更新 UI
 */
fun runOnMainThread(runnable: Runnable) {
    Handler(Looper.getMainLooper()).post(runnable)
}