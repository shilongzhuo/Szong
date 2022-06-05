package com.example.szong.api.music.artist.netease

import androidx.annotation.Keep

/**
 * 歌手简单信息和歌曲
 */
@Keep
data class ArtistsData(
    val artist: ArtistData,
    // val hotSongs: HotSongsData,
    val more: Boolean,
    val code: Int
) {
    data class ArtistData(
        val briefDesc: String?,
        val name: String,
        val id: Int
    )

}
