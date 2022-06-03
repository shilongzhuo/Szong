package com.example.szong.api.music.song.fm.netease


import com.example.szong.config.API_AUTU
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.util.net.MagicHttp
import com.example.szong.util.net.status.ErrorCode
import com.google.gson.Gson
import okhttp3.FormBody
import kotlin.Exception

object PersonalFM {

    private const val API = "https://music.163.com/api/v1/radio/get"
    private const val TEST_API = "$API_AUTU/personal_fm"

    /**
     * 获取 私人 FM
     * 失败的回调 [failure]
     */
    fun get(success: (ArrayList<StandardSongData>) -> Unit, failure: (Int) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("crypto", "weapi")
            .add("cookie", NeteaseUser.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .build()
        MagicHttp.OkHttpManager().newPost(TEST_API, requestBody, {
            try {
                val personFMData = Gson().fromJson(it, PersonFMData::class.java)
                success(personFMData.toSongList())
            } catch (e: Exception) {
               failure(ErrorCode.ERROR_JSON)
            }
        }, {
            failure(ErrorCode.ERROR_MAGIC_HTTP)
        })
    }

}