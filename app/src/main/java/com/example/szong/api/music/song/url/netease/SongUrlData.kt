package com.example.szong.api.music.song.url.netease

data class SongUrlData(
    val data: ArrayList<UrlData>,
    val code: Int
) {
    data class UrlData(
        val id: Long,
        val url: String?,
        val br: Int,
        val size: Long,
        val type: String?
    )
}


