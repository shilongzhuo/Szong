package com.example.szong.api.music.lyric.netease

import androidx.annotation.Keep

/**
 * 网易云歌词
 */
@Keep
data class LyricData(
    val lrc: LrcData?,
    val tlyric: LrcData?,
    val code: Int
) {
    @Keep
    data class LrcData(
        val lyric: String
    )
}
