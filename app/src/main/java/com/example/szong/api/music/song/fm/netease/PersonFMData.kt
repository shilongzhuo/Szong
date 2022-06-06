package com.example.szong.api.music.song.fm.netease

import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.StandardSongData


data class PersonFMData(
    val data: ArrayList<Data>
) {
    data class Data(
        val name: String,
        val id: Long,
        val artists: ArrayList<StandardSongData.StandardArtistData>,
        val album: AlbumData
    ) {
        data class AlbumData(
            val name: String,
            val picUrl: String,
        )
    }
}

fun PersonFMData.toSongList(): ArrayList<StandardSongData> {
    val songList = ArrayList<StandardSongData>()
    this.data.forEach {
        val song = StandardSongData(
            SOURCE_NETEASE,
            it.id.toString(),
            it.name,
            it.album.picUrl,
            it.artists,
            null,
            null,
            null
        )
        songList.add(song)
    }
    return songList
}
