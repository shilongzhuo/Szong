package com.example.szong.service.media.music

import android.content.ContentUris
import android.net.Uri
import com.example.szong.App
import com.example.szong.manager.music.Api
import com.example.szong.config.AppConfig
import com.example.szong.api.music.song.search.kuwo.SearchSong
import com.example.szong.api.music.song.url.netease.SongUrl
import com.example.szong.api.music.song.url.qq.PlayUrl
import com.example.szong.data.music.standard.*
import com.example.szong.data.music.LyricViewData
import com.example.szong.data.music.SearchLyric
import com.example.szong.plugin.PluginConstants
import com.example.szong.plugin.PluginSupport
import com.example.szong.util.app.runOnMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ServiceSongUrl {

    inline fun getUrlProxy(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        getUrl(song) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    success.invoke(it)
                }
            }
        }
    }

    inline fun getUrl(song: StandardSongData, crossinline success: (Any?) -> Unit) {
        PluginSupport.setSong(song)
        val pluginUrl = PluginSupport.apply(PluginConstants.POINT_SONG_URL)
        if (pluginUrl != null && pluginUrl is String) {
            success.invoke(pluginUrl)
            return
        }
        when (song.source) {
            SOURCE_NETEASE -> {
                GlobalScope.launch {
                    if (song.neteaseInfo?.pl == 0) {
                        if (App.mmkv.decodeBool(AppConfig.AUTO_CHANGE_RESOURCE)) {
                            GlobalScope.launch {
                                val url = getUrlFromOther(song)
                                success.invoke(url)
                            }
                        } else {
                            success.invoke(null)
                        }
                    } else {
                        var url = ""
                        if (url.isEmpty())
                            SongUrl.getSongUrlCookie(song.id ?: "") {
                                success.invoke(it)
                            }
                        else
                            success.invoke(url)
                    }
                }
            }
            SOURCE_LOCAL -> {
                val id = song.id?.toLong() ?: 0
                val contentUri: Uri =
                    ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                success.invoke(contentUri)
            }
            SOURCE_QQ -> {
                GlobalScope.launch {
                    success.invoke(PlayUrl.getPlayUrl(song.id ?: ""))
                }
            }
            SOURCE_szong -> {
                GlobalScope.launch {
                    success.invoke(song.szongInfo?.url)
                }
            }
            SOURCE_KUWO -> {
                GlobalScope.launch {
                    val url = SearchSong.getUrl(song.id ?: "")
                    success.invoke(url)
                }
            }
            SOURCE_NETEASE_CLOUD -> {
                SongUrl.getSongUrlCookie(song.id ?: "") {
                    success.invoke(it)
                }
            }
            else -> success.invoke(null)
        }
    }

    fun getLyric(song: StandardSongData, success: (LyricViewData) -> Unit) {
        if (song.source == SOURCE_NETEASE) {
            App.cloudMusicManager.getLyric(song.id?.toLong() ?: 0) { lyric ->
                runOnMainThread {
                    val l = LyricViewData(lyric.lrc?.lyric ?: "", lyric.tlyric?.lyric ?: "")
                    success.invoke(l)
                }
            }
        } else {
            SearchLyric.getLyricString(song) { string ->
                runOnMainThread {
                    success.invoke(LyricViewData(string, ""))
                }
            }
        }
    }

    suspend fun getUrlFromOther(song: StandardSongData): String {
        Api.getFromKuWo(song)?.apply {
            SearchSong.getUrl(id ?: "").let {
                return it
            }
        }
        Api.getFromQQ(song)?.apply {
            PlayUrl.getPlayUrl(id ?: "").let {
                return it
            }


        }
        return ""
    }

    private fun getArtistName(artists: List<StandardSongData.StandardArtistData>?): String {
        val sb = StringBuilder()
        artists?.forEach {
            if (sb.isNotEmpty()) {
                sb.append(" ")
            }
            sb.append(it.name)
        }
        return sb.toString()
    }

}