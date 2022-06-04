package com.example.szong.util.data

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

val String.Companion.EMPTY
    get() = ""

/**
 * 判断是否是中文字符
 */
fun Char.isChinese(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)
    if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    ) // 中日韩象形文字
    {
        return true
    }
    return false
}

fun String.md5(): String {
    if (this.isEmpty()) {
        return ""
    }
    try {
        val md5 = MessageDigest.getInstance("MD5")
        val bytes: ByteArray = md5.digest(this.toByteArray())
        val result = StringBuilder()
        for (b in bytes) {
            var temp = Integer.toHexString(b and 0xff)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            result.append(temp)
        }
        return result.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

infix fun Byte.and(mask: Int): Int = toInt() and mask
