package com.example.szong.api.music.playlist.album.netease

import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.api.music.song.search.netease.Album
import com.example.szong.api.music.song.search.netease.Song


data class NeteaseAlbumResult(
    val code:Int,
    val songs:List<Song>,
    val album: Album
) {
    fun switchToStandardSongs():List<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        songs.forEach{
            list.add(it.switchToStandard())
        }
        return list
    }
}


