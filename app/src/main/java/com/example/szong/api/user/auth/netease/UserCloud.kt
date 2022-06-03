package com.example.szong.api.user.auth.netease

import com.example.szong.config.API_AUTU
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.music.netease.data.UserCloudData
import com.example.szong.util.net.MagicHttp
import com.example.szong.util.net.status.ErrorCode
import com.google.gson.Gson
import okhttp3.FormBody

/**
 * 用户云盘数据
 */
object UserCloud {

    private const val TAG = "UserCloud"

    private const val API = "https://music.163.com/api/v1/cloud/get"
    private const val TEST_API = "${API_AUTU}/user/cloud"

    fun getUserCloud(offset: Int, success: (UserCloudData) -> Unit, failure: (Int) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("cookie", NeteaseUser.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("limit", "50")
            .add("offset", "$offset")
            .build()
        var api = NeteaseUser.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
        }
        MagicHttp.OkHttpManager().newPost("${api}/user/cloud", requestBody, {
            // Log.e(TAG, "getUserCloud: $it", )
            try {
                val userCloudData = Gson().fromJson(it, UserCloudData::class.java)
                success.invoke(userCloudData)
            } catch (e: Exception) {
                failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {

        })
    }

}