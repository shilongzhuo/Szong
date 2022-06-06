package com.example.szong.service.media.music

import androidx.lifecycle.MutableLiveData
import com.example.szong.App
import com.example.szong.database.room.PlayQueueData
import com.example.szong.data.music.standard.StandardSongData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



object PlayQueue {

    /* 随机播放的队列 */
    var currentQueue = MutableLiveData<ArrayList<StandardSongData>>().also {
        it.value = ArrayList()
    }

    private var normalQueue = MutableLiveData<ArrayList<StandardSongData>>().also {
        it.value = ArrayList()
    }

    private var queue = ArrayList<StandardSongData>()

    fun setNormal(list: ArrayList<StandardSongData>) {
        queue = list
        normalQueue.value = list
        normal()
    }

    /**
     * 随机播放
     */
    fun random() {
        val shuffle = currentQueue.value
        shuffle?.shuffle()
        currentQueue.value = shuffle
        savePlayQueue()
    }

    fun normal() {
        currentQueue.value?.clear()
        currentQueue.value?.addAll(queue)
        savePlayQueue()
    }

    /**
     * 保存歌单到数据库
     */
    private fun savePlayQueue() {
        GlobalScope.launch {
            App.appDatabase.playQueueDao().loadAll().forEach {
                App.appDatabase.playQueueDao().deleteById(it.songData.id ?: "")
            }
            currentQueue.value?.let {
                for (song in 0..it.lastIndex) {
                    App.appDatabase.playQueueDao().insert(PlayQueueData(it[song]))
                }
            }
        }
    }

}