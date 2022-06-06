package com.example.szong.util.data

import java.util.*

/**
 * List 转 ArrayList
 * @return ArrayList
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    arrayList.addAll(this)
    return arrayList
}

/**
 * List 切割
 * 平均分配
 */
fun <T> List<T>.averageAssignFixLength(splitItemNum: Int): List<List<T>> {
    val result = ArrayList<List<T>>()
    if (this.run { isNotEmpty() } && splitItemNum > 0) {
        if (this.size <= splitItemNum) {
            // 源List元素数量小于等于目标分组数量
            result.add(this)
        } else {
            // 计算拆分后list数量
            val splitNum =
                if (this.size % splitItemNum == 0) this.size / splitItemNum else this.size / splitItemNum + 1

            var value: List<T>? = null
            for (i in 0 until splitNum) {
                value = if (i < splitNum - 1) {
                    this.subList(i * splitItemNum, (i + 1) * splitItemNum)
                } else {
                    // 最后一组
                    this.subList(i * splitItemNum, this.size)
                }
                result.add(value)
            }
        }
    }
    return result
}

/**
 * List 中上一项
 */
fun <T> List<T>.previous(current: T?): T? {
    if (this.isEmpty() || current == null) {
        return null
    }
    when (val position = this.indexOf(current)) {
        -1 -> return null
        0 -> {
            this[this.lastIndex]?.let {
                return it
            }
        }
        else -> {
            this[position - 1]?.let {
                return it
            }
        }
    }
    return null
}

/**
 * List 中下一项
 * 传入当前项 [current]，返回下一项（可能为空）
 */
fun <T> List<T>.next(current: T?): T? {
    if (this.isEmpty() || current == null) {
        return null
    }
    when (val position = this.indexOf(current)) {
        -1 -> return null
        this.lastIndex -> {
            this[0]?.let {
                return it
            }
        }
        else -> {
            this[position + 1]?.let {
                return it
            }
        }
    }
    return null
}

/**
 * ArrayList 中上一项
 */
fun <T> ArrayList<T>.previous(current: T?): T? {
    if (this.isEmpty() || current == null) {
        return null
    }
    when (val position = this.indexOf(current)) {
        -1 -> return null
        0 -> {
            this[this.lastIndex]?.let {
                return it
            }
        }
        else -> {
            this[position - 1]?.let {
                return it
            }
        }
    }
    return null
}

/**
 * ArrayList 中下一项
 * 传入当前项 [current]，返回下一项（可能为空）
 */
fun <T> ArrayList<T>.next(current: T?): T? {
    if (this.isEmpty() || current == null) {
        return null
    }
    when (val position = this.indexOf(current)) {
        -1 -> return null
        this.lastIndex -> {
            this[0]?.let {
                return it
            }
        }
        else -> {
            this[position + 1]?.let {
                return it
            }
        }
    }
    return null
}

/**
 * 字节数组转 16 进制字符串
 */
fun ByteArray.toHex(): String? {
    val stringBuilder = StringBuilder("")
    if (this.isEmpty()) {
        return null
    }
    for (element in this) {
        val v = element.toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}