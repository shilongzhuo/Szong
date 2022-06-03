package com.example.szong.api.music.song.daily.netease
import com.example.szong.data.music.standard.StandardSongData

data class DailyRecommendSongData(
    val code: Int,
    val data: DataData,
) {
    data class DataData(
        val dailySongs: ArrayList<DailySongsData>,
    ) {
        data class DailySongsData(
            val name: String,
            val id: String,
            val ar: ArrayList<StandardSongData.StandardArtistData>,
            val al: AlbumData,
            val reason: String,

            val privilege: PrivilegeData,
        ) {
            data class AlbumData(
                val id: String,
                val name: String,
                val picUrl: String
            )
            data class PrivilegeData(
                val fee: Int,
                val pl: Int?,
                val flag: Int?,
                val maxbr: Int?,
            )
        }
    }
}

