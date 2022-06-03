package com.example.szong.data.music.standard

data class StandardSinger(
    val id: Long,
    val name: String,
    val picUrl: String,
    val briefDesc: String
)

data class StandardSingerPackage(
    val singer: StandardSinger,
    val songs: List<StandardSongData>
)
