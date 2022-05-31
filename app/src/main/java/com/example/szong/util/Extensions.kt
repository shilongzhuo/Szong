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