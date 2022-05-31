package com.example.szong.music.standard.data

import androidx.annotation.Keep

/**
 * 专辑封面
 */
@Keep
data class StandardAlbum (
    val name: String,
    val id: Long,
    val size: Int,
    val picUrl: String,
    val publishTime: Long,
    val company: String,
    val artName:String,
    val description:String
)

/**
 * 专辑
 */
data class StandardAlbumPackage(
    val album: StandardAlbum,
    val songs: List<StandardSongData>
)