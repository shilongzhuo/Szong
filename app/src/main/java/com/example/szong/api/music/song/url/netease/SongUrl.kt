package com.example.szong.api.music.song.url.netease

import com.example.szong.config.API_AUTU
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.util.net.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

object SongUrl {

    const val API = "${API_AUTU}/song/url?id=33894312"

    fun getSongUrlCookie(id: String, success: (String) -> Unit) {
        var api = NeteaseUser.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
        }
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("cookie", NeteaseUser.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("id", id)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/song/url", requestBody, {
            try {
                val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
                success.invoke(songUrlData.data[0].url ?: "")
            } catch (e: Exception) {
                // failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {

        })
    }

}