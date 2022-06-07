package com.example.szong.data.music.standard

/**
 * 专辑封面
 */
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