package com.example.szong.music.netease
import android.content.Context
import com.example.szong.config.API_AUTU
import com.example.szong.util.net.MagicHttp
import com.google.gson.Gson

/**
 * 获取网易云推荐歌单
 */
object PlaylistRecommend {

    fun getPlaylistRecommend(context: Context, success: (ArrayList<PlaylistRecommendDataResult>) -> Unit, failure: () -> Unit) {

        val url = "${API_AUTU}/personalized?limit=16"

        MagicHttp.OkHttpManager().getByCache(context, url, {
            try {
                val playlistRecommendDataResultArrayList = Gson().fromJson(it, PlaylistRecommendData::class.java).result
                success.invoke(playlistRecommendDataResultArrayList)
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })

    }

    data class PlaylistRecommendData(
        val result: ArrayList<PlaylistRecommendDataResult>
    )

    data class PlaylistRecommendDataResult(
        val id: Long,
        val picUrl: String,
        val name: String,
        val playCount: Long, // 播放数
    )

}

