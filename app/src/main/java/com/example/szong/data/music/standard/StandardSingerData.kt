package com.example.szong.data.music.standard

data class StandardSingerData(
    val id: Long,
    val name: String,
    val picUrl: String,
    val briefDesc: String
)

data class StandardSingerPackageData(
    val singer: StandardSingerData,
    val songs: List<StandardSongData>
)
