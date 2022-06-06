package com.example.szong.util.data

import org.json.JSONObject

fun JSONObject.getStr(key: String, defValue: String = "") = if (this.isNull(key)) {
    defValue
} else {
    this.getString(key)
}

fun JSONObject.getIntOrNull(key: String, defValue: Int = 0) = if (this.isNull(key)) {
    defValue
} else {
    this.getInt(key)
}

