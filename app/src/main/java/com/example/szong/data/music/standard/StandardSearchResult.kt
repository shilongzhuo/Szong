package com.example.szong.data.music.standard

data class StandardSearchResult(
    val songs:List<StandardSongData>,
    val playlist:List<StandardPlaylist>,
    val albums:List<StandardAlbum>,
    val singers:List<StandardSingerData>
)
