package com.example.szong.manager.music

import android.util.Log
import androidx.annotation.Keep
import com.example.szong.api.music.lyric.netease.LyricData
import com.example.szong.App
import com.example.szong.api.music.artist.ArtistsData
import com.example.szong.api.music.song.search.netease.SearchDefaultData
import com.example.szong.api.music.song.search.netease.SearchHotData
import com.example.szong.api.music.song.url.netease.SongUrlData
import com.example.szong.api.user.auth.netease.UserDetailData
import com.example.szong.api.user.letter.netease.PrivateLetterData
import com.example.szong.config.*
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.data.music.CommentData
import com.example.szong.util.app.loge

import com.example.szong.util.net.MagicHttp
import com.example.szong.util.system.getCurrentTime
import com.example.szong.widget.toast

import com.google.gson.Gson

@Keep
class CloudMusicManager {

    companion object {
        private const val URL_PRIVATE_LETTER = "${API_DEFAULT}/msg/private" // 私信
    }

    /**
     * 时间戳
     */
    private fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }

    fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit) {
        val url = "$API_MUSIC_ELEUU/comment/music?id=${id}&limit=20&offset=0${timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val commentData = Gson().fromJson(it, CommentData::class.java)
            success.invoke(commentData)
        }, {

        })
    }

    fun getUserDetail(userId: Long, success: (UserDetailData) -> Unit, failure: () -> Unit) {
        val url = "${API_AUTU}/user/detail?uid=${userId}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                NeteaseUser.szongUser.updateFromNet(userDetail)
                NeteaseUser.vipType = userDetail.profile.vipType
                if (userDetail.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(userDetail)
                }
            } catch (e: java.lang.Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    fun getUserDetail(
        uid: String,
        success: (result: com.example.szong.data.music.UserDetailData) -> Unit,
        failure: (error: String) -> Unit
    ) {
        MagicHttp.OkHttpManager().newGet("${API_AUTU}/user/detail?uid=$uid", {
            try {
                val userDetailData = Gson().fromJson(it, com.example.szong.data.music.UserDetailData::class.java)
                when (userDetailData.code) {
                    400 -> failure.invoke("获取用户详细信息错误")
                    404 -> failure.invoke("用户不存在")
                    else -> success.invoke(userDetailData)
                }
            } catch (e: java.lang.Exception) {
                failure.invoke("解析错误")
            }
        }, {
            failure.invoke("MagicHttp 错误\n${it}")
            Log.e("无法连接到服务器", it)
        })
    }

    fun likeSong(songId: String, success: () -> Unit, failure: () -> Unit) {
        val cookie = NeteaseUser.cookie
        val url = "${API_SZONG}/like?id=${songId}&cookie=${cookie}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("喜欢音乐返回值：${it}")
                val code = Gson().fromJson(it, CodeData::class.java).code
                if (code != 200) {
                    failure.invoke()
                } else {
                    success.invoke()
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    /**
     * 发送评论
     * @param t 1 发送 2 回复
     * @param type 0 歌曲 1 mv 2 歌单 3 专辑 4 电台 5 视频 6 动态
     * @param id 对应资源 id
     * @param content 要发送的内容
     * @param commentId 回复的评论id (回复评论时必填)
     */
    fun sendComment(
        t: Int,
        type: Int,
        id: String,
        content: String,
        commentId: Long,
        success: (CodeData) -> Unit,
        failure: () -> Unit
    ) {
        val cookie = NeteaseUser.cookie
        var url = "${API_DEFAULT}/comment?t=${t}&type=${type}&id=${id}&content=${content}&cookie=${cookie}"
        if (commentId != 0L) {
            url += "&commentId=${commentId}"
        }
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("评论返回$it")
                val codeData = Gson().fromJson(it, CodeData::class.java)
                if (codeData.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(codeData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    fun getPrivateLetter(success: (PrivateLetterData) -> Unit, failure: () -> Unit) {
        val cookie = NeteaseUser.cookie
        val url = "${URL_PRIVATE_LETTER}?cookie=${cookie}"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                loge("url:[${url}]私信返回" + it)
                val privateLetterData = Gson().fromJson(it, PrivateLetterData::class.java)
                if (privateLetterData.code != 200) {
                    failure.invoke()
                } else {
                    success.invoke(privateLetterData)
                }
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {
            failure.invoke()
        })
    }

    fun getPicture(url: String, heightOrWeight: Int): String {
        return "${url}?param=${heightOrWeight}y${heightOrWeight}"
    }

    fun getSearchDefault(success: (SearchDefaultData) -> Unit) {
        val url = CloudMusicAPIConfig.SEARCH_DEFAULT
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val searchDefaultData = Gson().fromJson(it, SearchDefaultData::class.java)
                if (searchDefaultData.code == 200) {
                    success.invoke(searchDefaultData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getSearchHot(success: (SearchHotData) -> Unit) {
        val url = CloudMusicAPIConfig.SEARCH_HOT_DETAIL
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val searchHotData = Gson().fromJson(it, SearchHotData::class.java)
                if (searchHotData.code == 200) {
                    success.invoke(searchHotData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getArtists(artistId: Long, success: (ArtistsData) -> Unit) {
        val url = CloudMusicAPIConfig.ARTISTS + "?id=$artistId"
        MagicHttp.OkHttpManager().newGet(url, {
            val artistsData = Gson().fromJson(it, ArtistsData::class.java)
            if (artistsData.code == 200) {
                success.invoke(artistsData)
            }
        }, {

        })
    }

    fun getLyric(songId: Long, success: (LyricData) -> Unit) {
        val url = CloudMusicAPIConfig.LYRIC + "?id=$songId"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val lyricData = Gson().fromJson(it, LyricData::class.java)
                if (lyricData.code == 200) {
                    success.invoke(lyricData)
                }
            } catch (e: Exception) {

            }
        }, {

        })
    }

    fun getSongInfo(id: String, success: (SongUrlData.UrlData) -> Unit) {
        val url = "${API_MUSIC_ELEUU}/song/url?id=${id}${timestamp()}"
        MagicHttp.OkHttpManager().newGet(url, {
            val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
            if (songUrlData.code == 200) {
                success.invoke(songUrlData.data[0])
            }
        }, {

        })
    }

    fun loginByUid(uid: String, success: () -> Unit) {
        getUserDetail(uid, {
            App.mmkv.encode(AppConfig.UID, it.profile?.userId!!.toLong())
            // UID 登录清空 Cookie
            NeteaseUser.cookie = ""
            success.invoke()
            // toast("登录成功${it.profile?.userId!!.toLong()}")
        }, {
            toast(it)
        })
    }

}