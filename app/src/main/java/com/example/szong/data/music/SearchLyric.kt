package com.example.szong.data.music

import com.example.szong.api.music.lyric.local.LocalLyricAPI
import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.SOURCE_QQ
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.util.net.MagicHttp
import com.google.gson.Gson

object SearchLyric {

    fun getLyricString(songData: StandardSongData, success: (String) -> Unit) {
        var url = ""
        when (songData.source) {
            SOURCE_NETEASE -> {
                url = "http://music.eleuu.com/lyric?id=${songData.id}"
            }
            SOURCE_QQ -> {
                url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=${songData.id}&format=json&nobase64=1"
            }
            else -> {
                LocalLyricAPI.getLyricWithQq(songData.name?:"") {
                    success.invoke(it)
                }
            }
        }

        MagicHttp.OkHttpManager().newGet(url, { response ->
            var lyric = response

            when (songData.source) {
                SOURCE_QQ -> {
                    if (Gson().fromJson(lyric, QQSongLyric::class.java).lyric != null) {
                        lyric = Gson().fromJson(lyric, QQSongLyric::class.java).lyric.toString()
                        success.invoke(lyric)
                    } else {
                        success.invoke("")
                    }
                }
                SOURCE_NETEASE -> {
                    if (Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc != null) {
                        lyric = Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc?.lyric.toString()
                        success.invoke(lyric)
                    } else {
                        success.invoke("")
                    }
                }
            }

        }, {

        })
    }

    /**
     * QQ 音乐歌词数据类
     */
    data class QQSongLyric(
        val lyric: String?
    )

    /**
     * 网易云歌词数据类
     */
    data class NeteaseSongLyric(
        val lrc: NeteaseSongLrc?
    ) {
        data class NeteaseSongLrc(
            val lyric: String?
        )
    }

}
