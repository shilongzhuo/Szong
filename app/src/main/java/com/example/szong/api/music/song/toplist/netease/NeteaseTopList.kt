package com.example.szong.api.music.song.toplist.netease

import android.content.Context
import com.example.szong.config.API_AUTU
import com.example.szong.util.net.MagicHttp
import com.google.gson.Gson

/**
 * 网易音乐排行榜
 */
object NeteaseTopList {

    private const val API = "${API_AUTU}/toplist/detail"

    fun getTopList(context: Context, success: (TopListData) -> Unit, failure: () -> Unit) {
        MagicHttp.OkHttpManager().getByCache(context, API, {
            try {
                val topListData = Gson().fromJson(it, TopListData::class.java)
                if (topListData.code == 200) {
                    success.invoke(topListData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

}