import com.example.szong.util.dp2px


/**
 * 拓展函数
 */

val String.Companion.EMPTY
    get() = ""


/**
 * dp
 */
fun Int.dp(): Int {
    return dp2px(this.toFloat()).toInt()
}

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