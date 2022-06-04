package com.example.szong.util.app

import android.content.Context
import android.content.Intent

object BroadcastUtil {

    fun send(context: Context, action: String) {
        val intent = Intent(action)
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
    }

}