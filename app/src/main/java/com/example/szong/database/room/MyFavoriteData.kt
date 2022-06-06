package com.example.szong.database.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.szong.data.music.standard.StandardSongData

/**
 * ‘我的收藏’的数据实体
 * 内嵌‘歌曲’数据实体
 */
@Entity
@TypeConverters(StandardArtistDataConverter::class)
data class MyFavoriteData(
    @Embedded
    var songData: StandardSongData
) {

    @PrimaryKey(autoGenerate = true)
    var databaseId: Long = 0

}
