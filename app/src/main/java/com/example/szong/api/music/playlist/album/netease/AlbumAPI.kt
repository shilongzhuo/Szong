package com.example.szong.api.music.playlist.album.netease

import com.example.szong.data.music.standard.StandardAlbumPackage
import com.example.szong.util.net.HttpUtils

object AlbumAPI {
    suspend fun getAlbumSongs(id:Long): StandardAlbumPackage? {
        val url = "https://olbb.vercel.app/album?id=${id}"
        HttpUtils.get(url, NeteaseAlbumResult::class.java)?.let {
            return StandardAlbumPackage(it.album.switchToStandard(), it.switchToStandardSongs())
        }
        return null
    }
}