package com.example.szong.api.music.song.search.netease

import androidx.annotation.Keep

@Keep
data class SearchDefaultData(
    val code: Int,
    val message: String?,
    val data: DataData
) {
    data class DataData(
        val showKeyword: String,
        val realkeyword: String,
        val searchType: Int,
        val action: Int,
        val alg: String,
        val gap: Int,
        val source: String?
    )
}
