package com.example.szong.music.standard.data

import com.example.szong.music.standard.data.StandardAlbum

data class StandardSearchResult(
    val songs:List<StandardSongData>,
    val playlist:List<StandardPlaylist>,
    val albums:List<StandardAlbum>,
    val singers:List<StandardSinger>
)
