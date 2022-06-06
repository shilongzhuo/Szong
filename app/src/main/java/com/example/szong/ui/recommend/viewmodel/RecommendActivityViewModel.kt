package com.example.szong.ui.recommend.viewmodel

import androidx.lifecycle.ViewModel
import com.example.szong.App
import com.example.szong.api.music.song.daily.netease.DailyRecommendSongData
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.util.net.MagicHttp
import com.example.szong.widget.toast
import com.google.gson.Gson

class RecommendActivityViewModel: ViewModel() {

    /**
     * 获取日推
     */
    fun getRecommendSong(success: (DailyRecommendSongData) -> Unit, failure: (String) -> Unit) {
        if (NeteaseUser.hasCookie) {
            val url = "https://music.163.com/api/v3/discovery/recommend/songs"+ "?crypto=weapi&withCredentials=true" + "&cookie="+ NeteaseUser.cookie
            // + "&timestamp=${getCurrentTime()}"
            // val url = API_DEFAULT + "/recommend/songs?cookie=" + MyApplication.userManager.getCloudMusicCookie()
            MagicHttp.OkHttpManager().getByCache(App.context, url, 600,  {
                // loge(url)
                try {
                    val data = Gson().fromJson(it, DailyRecommendSongData::class.java)
                    when (data.code) {
                        200 -> success.invoke(data)
                        301 -> failure.invoke("错误代码：${data.code}，尝试重新登录")
                        else -> failure.invoke("错误代码：${data.code}")
                    }
                } catch (e: Exception) {
                    failure.invoke("获取失败")
                }
            }, {
                toast("网络失败")
            })
        } else {
            toast("UID 离线状态无法获取每日推荐，请使用手机号登录")
        }
    }

}