package com.example.szong.api.music.song.daily.netease

import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.StandardSongData

fun ArrayList<DailyRecommendSongData.DataData.DailySongsData>.toStandardSongDataArrayList():
        ArrayList<StandardSongData> {
    val standardSongDataArrayList = ArrayList<StandardSongData>()
    this.forEach {
        val songData = StandardSongData(
            SOURCE_NETEASE,
            it.id,
            it.name,
            it.al.picUrl,
            it.ar,
            StandardSongData.NeteaseInfo(
                it.privilege.fee,
                it.privilege.pl,
                it.privilege.flag,
                it.privilege.maxbr),
            null,
            null
        )
        standardSongDataArrayList.add(songData)
    }
    return standardSongDataArrayList
}
