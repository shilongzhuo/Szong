package com.example.szong.database.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.example.szong.music.standard.data.StandardSongData

/**
 * ‘播放列表‘ 数据实体
 */

@Entity
@TypeConverters(StandardArtistDataConverter::class, StandardSongDataConverter::class)
data class PlayQueueData(
    @Embedded
    var songData: StandardSongData) {
    @PrimaryKey(autoGenerate = true)
    var databaseId: Long = 0
}

fun List<PlayQueueData>.toSongList(): ArrayList<StandardSongData> {
    val list = ArrayList<StandardSongData>()
    this.forEach {
        list.add(it.songData)
    }
    return list
}