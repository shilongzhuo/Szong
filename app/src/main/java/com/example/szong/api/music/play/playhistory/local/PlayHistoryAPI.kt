package com.example.szong.api.music.play.playhistory.local

import android.os.Parcelable
import com.example.szong.App
import com.example.szong.config.AppConfig
import com.example.szong.data.music.standard.StandardSongData
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.TestOnly

/**
 * 播放历史单例类
 */
object PlayHistoryAPI {

    private var playHistory = PlayHistoryData(ArrayList())

    /**
     * 加入播放历史
     */
    @TestOnly
    fun addPlayHistory(songData: StandardSongData) {
        // 如果不在歌单中，就添加
        if (songData !in playHistory.list) {
            playHistory.list.add(0, songData)
            // playlist.add(songData)
        }
        App.mmkv.encode(AppConfig.PLAY_HISTORY, playHistory)

    }

    /**
     * 读取播放历史
     */
    fun readPlayHistory(): ArrayList<StandardSongData> {
        playHistory = App.mmkv.decodeParcelable(AppConfig.PLAY_HISTORY, PlayHistoryData::class.java, PlayHistoryData(
            ArrayList()
        )
        )
        return playHistory.list
    }

    @Parcelize
    data class PlayHistoryData(
        val list: ArrayList<StandardSongData>
    ): Parcelable

}