package com.example.szong.api.music.song.search.netease

import com.example.szong.data.music.standard.*
import com.example.szong.util.data.decode.Base64
import com.example.szong.util.data.decode.MD5
import kotlin.experimental.xor

private val byte1 = "3go8&$8*3*3h0k(2)2".toByteArray()

fun getNeteasePicUrl(id: Long): String {
    val byte2 = id.toString().toByteArray()
    for (i in byte2.indices) {
        byte2[i] = byte2[i] xor byte1[i % byte1.size]
    }
    val m = MD5.getMD5Code(byte2)
    var result = Base64.encode(m!!)
    result = result.replace("/", "_").replace("+", "-")
    return "https://p3.music.126.net/$result/$id.jpg"
}

class NeteaseSearchResult(
    val code: Int,
    val result: Result
)

data class Result(
    val songCount: Int,
    val songs: List<Song>?,
    val playlists: List<Playlist>?,
    val playlistCount: Int,
    val albums: List<Album>?,
    val albumCount: Int,
    val artists:List<Artists>?,
    val artistCount: Int
) {
    fun switchToStandardSongs():List<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        if (songs != null) {
            for (song in songs) {
                list.add(song.switchToStandard())
            }
        }
        return list
    }

    fun switchToStandardPlaylist():List<StandardPlaylist> {
        val list = ArrayList<StandardPlaylist>()
        if (playlists != null) {
            for (playlist in playlists) {
                list.add(playlist.toStandardPlaylist())
            }
        }
        return list
    }

    fun switchToStandardAlbums():List<StandardAlbum> {
        val list = ArrayList<StandardAlbum>()
        if (albums != null) {
            for (album in albums) {
                list.add(album.switchToStandard())
            }
        }
        return list
    }

    fun switchToStandardSingers(): List<StandardSinger> {
        val list = ArrayList<StandardSinger>()
        if (artists != null) {
            for (singer in artists) {
                list.add(singer.switchToStandard())
            }
        }
        return list
    }

    fun toStandardResult(): StandardSearchResult {
        return StandardSearchResult(switchToStandardSongs(), switchToStandardPlaylist(), switchToStandardAlbums(), switchToStandardSingers())
    }
}

data class Playlist(
    val id: Long,
    val name: String,
    val coverImgUrl: String?,
    val playCount: Long,
    val description: String?,
    val trackCount: Int,
    val creator: Creator
) {
    fun toStandardPlaylist(): StandardPlaylist {
        return StandardPlaylist(id,name, coverImgUrl?:"",description?:"", creator.nickname?:"", trackCount, playCount)
    }
}

data class Creator(
    val userId: Long,
    val nickname: String
)

data class Song(
    val al: Al?,
    val ar: List<Ar>,
    val copyright: Long,
    val fee: Int,
    val id: Long,
    val name: String,
    val privilege: Privilege?,
) {
    fun switchToStandard(): StandardSongData {
        return StandardSongData(SOURCE_NETEASE, id.toString(), name, al?.getImageUrl(), getArtList(), getNeteaseInfo(), null, null)
    }

    private fun getNeteaseInfo(): StandardSongData.NeteaseInfo {
        return StandardSongData.NeteaseInfo(fee, privilege?.pl, 0, privilege?.maxbr)
    }

    private fun getArtList():ArrayList<StandardSongData.StandardArtistData> {
        val list = ArrayList<StandardSongData.StandardArtistData>()
        for (art in ar) {
            list.add(art.switchToStandard())
        }
        return list
    }
}

data class Al(
    val id: Long,
    val name: String,
    val pic: Long,
    val pic_str: Long,
    val picUrl: String?,
) {
    fun getImageUrl():String {
        return picUrl?: getNeteasePicUrl(pic_str)
    }
}

data class Ar(
    val id: Long,
    val name: String,
    val cover: String?,
    val briefDesc: String?
) {
    fun switchToStandard(): StandardSongData.StandardArtistData{
        return StandardSongData.StandardArtistData(id, name)
    }

    fun switchToStandardSinger(): StandardSinger {
        return StandardSinger(id,name,cover?:"",briefDesc?:"")
    }
}

data class Privilege(
    val maxbr: Int,
    val pl: Int
)

data class Album(
    val name: String,
    val id: Long,
    val size: Int,
    val picUrl: String?,
    val publishTime: Long,
    val company: String?,
    val artist: Ar?,
    val description: String?
) {
    fun switchToStandard() : StandardAlbum {
        return StandardAlbum(name, id, size, picUrl?:"", publishTime, company?:"", artist?.name ?:"", description?:"")

    }
}

data class Artists(
    val id: Long,
    val name: String,
    val picUrl: String,
    val albumSize: Int
) {
    fun switchToStandard() : StandardSinger {
        return StandardSinger(id, name, picUrl, "")
    }
}

data class ArtistsSongs(
    val songs:List<Song>,
    val more:Boolean,
    val total:Int,
    val code:Int
) {
    fun switchToStandardSongs():List<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        for (song in songs) {
            list.add(song.switchToStandard())
        }
        return list
    }
}

data class ArtistInfoResult(
    val code: Int,
    val data: ArtistInfo
)

data class ArtistInfo(
    val artist: Ar?
)

