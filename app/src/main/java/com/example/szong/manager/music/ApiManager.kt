package com.example.szong.manager.music

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.szong.App
import com.example.szong.R
import com.example.szong.api.music.playlist.album.netease.AlbumAPI
import com.example.szong.api.music.playlist.cloudplaylist.netease.CompatSearchData
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.api.music.playlist.cloudplaylist.netease.PlaylistAPI
import com.example.szong.api.music.playlist.cloudplaylist.netease.compatSearchDataToStandardPlaylistData
import com.example.szong.api.music.song.search.netease.ArtistInfoResult
import com.example.szong.api.music.song.search.netease.ArtistsSongs
import com.example.szong.api.music.song.search.netease.NeteaseSearchResult
import com.example.szong.api.music.song.search.qq.QqSearchSongAPI
import com.example.szong.api.user.auth.netease.NeteaseGetKey
import com.example.szong.api.user.auth.netease.NeteaseLoginResult
import com.example.szong.api.user.auth.netease.NeteaseQRCodeResult
import com.example.szong.api.user.auth.netease.NeteaseUserInfo
import com.example.szong.data.music.standard.*
import com.example.szong.data.music.DetailPlaylistData
import com.example.szong.data.music.DetailPlaylistInnerData
import com.example.szong.data.music.SearchType
import com.example.szong.ui.player.PlayerActivity
import com.example.szong.util.data.averageAssignFixLength
import com.example.szong.util.data.getIntOrNull
import com.example.szong.util.data.getStr
import com.example.szong.util.net.HttpUtils
import com.example.szong.widget.toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


fun toMap(vararg params: String): Map<String, String> {
    val map = HashMap<String, String>()
    var i = 0
    while (i < params.size) {
        map[params[i]] = params[i + 1]
        i += 2
    }
    return map
}

object ApiManager {

    private const val TAG = "API"

    private const val SPLIT_PLAYLIST_NUMBER = 1000 // 切割歌单
    private const val CHEATING_CODE = -460 // Cheating 错误

    suspend fun getPlayListInfo(id: Long): DetailPlaylistInnerData? {
        val url = "${getDefaultApi()}/playlist/detail?id=${id}"
        return HttpUtils.get(url, DetailPlaylistData::class.java, true)?.playlist
    }

    suspend fun getPlayList(id: Long, useCache: Boolean): PackedSongList {
        val params = HashMap<String, String>()
        params["id"] = id.toString()
        if (NeteaseUser.hasCookie) {
            params["cookie"] = NeteaseUser.cookie
        }
        val url = "${getDefaultApi()}/playlist/detail?hash=${params.hashCode()}"
        val result = HttpUtils.postWithCache(url, params, PlaylistAPI.PlaylistData::class.java, useCache)
        val trackIds = ArrayList<Long>()
        result?.result?.playlist?.trackIds?.forEach {
                trackId -> trackIds.add(trackId.id)
        }
        val list = ArrayList<StandardSongData>()
        if (trackIds.size > 0) {
            trackIds.averageAssignFixLength(SPLIT_PLAYLIST_NUMBER).forEach lit@ { subTrackIds ->
                Log.d(TAG, "subTrackIds size is ${subTrackIds.size}")
                val idsBuilder = StringBuilder()
                for (trackId in subTrackIds) {
                    if (idsBuilder.isNotEmpty()) {
                        idsBuilder.append(",")
                    }
                    idsBuilder.append(trackId)
                }
                val ids = idsBuilder.toString()
                val params = HashMap<String, String>()
                params["ids"] = ids
                params["cookie"] = NeteaseUser.cookie
                val data = HttpUtils.postWithCache("${getDefaultApi()}/song/detail?hash=${ids.hashCode()}",
                    params, CompatSearchData::class.java, useCache)
//                val data = HttpUtils.get("${getDefaultApi()}/song/detail?ids=${ids}", CompatSearchData::class.java)
                data?.result?.apply {
                    if (code == CHEATING_CODE) {
                        toast("-460 Cheating")
                        // 发生了欺骗立刻返回
                        return@lit
                    } else {
                        Log.i(TAG, "get result ${songs.size}")
                        list.addAll(compatSearchDataToStandardPlaylistData(this))
                    }
                }
            }

        }
        Log.d(TAG, "get playlist id $id, size:${list.size} , origin size:${trackIds.size}")
        return PackedSongList(list, result?.isCache?:false)
    }

    suspend fun searchMusic(keyword:String, type: SearchType): StandardSearchResult? {
        val url = "${getDefaultApi()}/cloudsearch?keywords=$keyword&limit=100&type=${SearchType.getSearchTypeInt(type)}"
        val result = HttpUtils.get(url, NeteaseSearchResult::class.java)
        return result?.result?.toStandardResult()
    }

    suspend fun getAlbumSongs(id:Long): StandardAlbumPackage? {
        return AlbumAPI.getAlbumSongs(id)
    }

    suspend fun getSingerSongs(id: Long): StandardSingerPackageData? {
        val songs = ArrayList<StandardSongData>()
        var result: ArtistsSongs?
        do {
            val url = "${getDefaultApi()}/artist/songs?id=$id&offset=${songs.size}"
            result = HttpUtils.get(url, ArtistsSongs::class.java, true)
            result?.let {
//                Log.d(TAG, "getSingerSongs result${result.songs.size} ")
                songs.addAll(it.switchToStandardSongs())
            }
        } while (result?.more == true && result.songs.isNotEmpty())

        HttpUtils.get("${getDefaultApi()}/artist/detail?id=$id", ArtistInfoResult::class.java, true)?.data?.artist?.let {
            return StandardSingerPackageData(it.switchToStandardSinger(), songs)
        }
        return null
    }

    suspend fun getOtherCPSong(song: StandardSongData): StandardSongData? {
        val r = getFromKuWo(song)
        if (r != null) {
            return r
        }
        return getFromQQ(song)
    }

    suspend fun getFromKuWo(song: StandardSongData): StandardSongData? {
        val songName = song.name?.replace(Regex("（.*）"), "")?.trim()?:""
        val artistName = song.artists?.first()?.name
        searchFromKuwo("$songName $artistName")?.forEach { res ->
            if (res.name == song.name ||  (res.name != null && res.name?.contains(songName) == true && res.name?.contains("伴奏") == false)) {
                val artName = res.artists?.first()?.name ?: ""
                song.artists?.let { artists ->
                    var checkSingerCount = 0
                    for (singer in artists) {
                        if (singer.name == artName || singer.name != null && artName.contains(singer.name)) {
                            checkSingerCount++
                        } else {
                            break
                        }
                    }
                    if (checkSingerCount == song.artists?.size) return res
                }

            }
        }
        return null
    }

    suspend fun getFromQQ(song: StandardSongData): StandardSongData? {
        val songName = song.name?.replace(Regex("（.*）"), "")?.trim()?:""
        val artistName = song.artists?.first()?.name
        searchFromQQ("$songName $artistName")?.data?.song?.list?.let {
            for (res in it) {
                if (res.songname == song.name || res.songname.contains(songName)) {
                    val nameBuffer = StringBuffer()
                    for (singer in res.singer) {
                        singer.name?.let { singerName -> nameBuffer.append(singerName) }
                    }
                    val names = nameBuffer.toString()
                    var checkSingerCount = 0
                    song.artists?.forEach forArtists@ { artist->
                        artist.name?.let { name ->
                            if (names.contains(name)) {
                                checkSingerCount++
                            } else {
                                return@forArtists
                            }
                        }
                    }
                    if (checkSingerCount == song.artists?.size) return res.switchToStandard()
                }
            }
        }
        return null
    }

    private suspend fun searchFromQQ(keywords: String): QqSearchSongAPI.QQSearch? {
        val url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=20&w=${keywords}"
        HttpUtils.get(url, String::class.java)?.let {
            var response = it.replace("callback(", "")
            if (response.endsWith(")")) {
                response = response.substring(0, response.lastIndex)
            }
            try {
                return Gson().fromJson(response, QqSearchSongAPI.QQSearch::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private suspend fun searchFromKuwo(keywords: String): List<StandardSongData>? {
        val url =
            "http://kuwo.cn/api/www/search/searchMusicBykeyWord?key=$keywords&pn=1&rn=50&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531"
        val header = mapOf(
            "Referer" to Uri.encode("http://kuwo.cn/search/list?key=$keywords"),
            "Cookie" to "kw_token=EUOH79P2LLK",
            "csrf" to "EUOH79P2LLK",
            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
        )
        HttpUtils.get(url, String::class.java,false , header)?.let {
            try {
                val resp = JSONObject(it)
                val songList = resp
                    .getJSONObject("data")
                    .getJSONArray("list")

                val standardSongDataList = ArrayList<StandardSongData>()
                // 每首歌适配
                (0 until songList.length()).forEach {
                    val songInfo = songList[it] as JSONObject
                    standardSongDataList.add(
                        com.example.szong.api.music.song.search.kuwo.KuSearchSongAPI.KuwoSearchData.SongData(
                            songInfo.getIntOrNull("rid").toString(),
                            songInfo.getStr("name", ""),
                            songInfo.getStr("artist", ""),
                            songInfo.getStr("pic", "")
                        ).switchToStandard()
                    )
                }
                return standardSongDataList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    suspend fun getLoginKey(): NeteaseGetKey? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/key?timestamp=${Date().time}", NeteaseGetKey::class.java)
    }

    suspend fun getLoginQRCode(key: String): NeteaseQRCodeResult? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/create?key=$key&qrimg=1&timestamp=${Date().time}", NeteaseQRCodeResult::class.java)
    }

    suspend fun checkLoginResult(key: String): NeteaseLoginResult? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/check?key=$key&timestamp=${Date().time}", NeteaseLoginResult::class.java)
    }

    suspend fun getUserInfo(cookie: String): NeteaseUserInfo? {
        return HttpUtils.post("${getLoginUrl()}/user/account", toMap("cookie", cookie) , NeteaseUserInfo::class.java)
    }

    private fun getLoginUrl() :String {
        return NeteaseUser.neteaseCloudMusicApi
    }

    private fun getDefaultApi() :String {
        var api = NeteaseUser.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
        }
        return api
    }

}

/**
 * 播放音乐
 */
fun playMusic(context: Context?, song: StandardSongData, songList: java.util.ArrayList<StandardSongData>, playAll: Boolean = false) {
    App.musicController.value?.setPersonFM(false)
    // 获取 position
    val position = if (songList.indexOf(song) == -1) {
        0
    } else {
        songList.indexOf(song)
    }
    // 歌单相同
    if (App.musicController.value?.getPlaylist() == songList) {
        // position 相同
        if (position == App.musicController.value?.getNowPosition() && context is Activity) {
            context.startActivity(Intent(context, PlayerActivity::class.java))
            context.overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        } else {
            App.musicController.value?.playMusic(song, playAll)
        }
    } else {
        // 设置歌单
        App.musicController.value?.setPlaylist(songList)
        // 播放歌单
        App.musicController.value?.playMusic(song, playAll)
    }
}