package com.example.szong.util.app

import com.example.szong.util.system.getCurrentTime

var lastClickTime = 0L
fun singleClick(during: Long = 200L, callBack: () -> Unit) {
    if (getCurrentTime() - lastClickTime > during) {
        callBack()
    }
    lastClickTime = getCurrentTime()
}