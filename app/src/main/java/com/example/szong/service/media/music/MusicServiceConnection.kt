package com.example.szong.service.media.music

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.szong.App
import com.example.szong.App.Companion.musicController
import com.example.szong.config.AppConfig
import com.example.szong.database.room.toSongList
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.util.app.runOnMainThread
import kotlin.concurrent.thread

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        musicController.value = p1 as MusicService.MusicController
        thread {
            // 恢复 SongData
            val recoverSong = App.mmkv.decodeParcelable(AppConfig.SERVICE_CURRENT_SONG, StandardSongData::class.java)
            val recoverProgress = App.mmkv.decodeInt(AppConfig.SERVICE_RECOVER_PROGRESS, 0)
            val recoverPlayQueue = App.appDatabase.playQueueDao().loadAll().toSongList()
            recoverSong?.let { song ->
                // recover = true
                if (recoverSong in recoverPlayQueue) {
                    runOnMainThread {
                        musicController.value?.let {
                            it.setRecover(true)
                            it.setRecoverProgress(recoverProgress)
                            it.setPlaylist(recoverPlayQueue)
                            it.playMusic(song)
                        }
                    }
                }
            }
        }
    }

    /**
     * 服务意外断开连接
     */
    override fun onServiceDisconnected(p0: ComponentName?) {
        musicController.value = null
    }

}