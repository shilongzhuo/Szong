package com.example.szong.api.music.lyric.local

import com.example.szong.App
import com.example.szong.config.AppConfig
import com.example.szong.api.music.song.search.qq.QqSearchSongAPI
import com.example.szong.data.music.SearchLyric


/**
 * 歌词适配
 * 为本地音乐添加来自网络的歌词
 */
object LocalLyricAPI {

    /**
     * 获取网络歌词
     * 传入名称
     */
    fun getLyricWithQq(name: String, success: (String) -> Unit) {
        if (App.mmkv.decodeBool(AppConfig.PARSE_INTERNET_LYRIC_LOCAL_MUSIC, true)) {
            // 调用一次 QQ 搜索
            QqSearchSongAPI.search(name) {
                if (it.isNotEmpty()) {
                    SearchLyric.getLyricString(it[0]) { string ->
                        success.invoke(string)
                    }
                } else {
                    success.invoke("")
                }
            }
        }
    }

}