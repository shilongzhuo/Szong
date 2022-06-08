package com.example.szong.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.szong.api.music.playlist.localplaylist.local.LocalPlaylistAPI
import com.example.szong.config.API_MUSIC_ELEUU
import com.example.szong.data.music.PlaylistData
import com.example.szong.data.music.UserPlaylistData
import com.example.szong.data.music.standard.StandardPlaylistData
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.util.net.HttpUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyFragmentViewModel : ViewModel() {

    // 用户歌单
    var userPlaylistList = MutableLiveData<ArrayList<PlaylistData>>()
    var localPlaylistList = MutableLiveData<ArrayList<StandardPlaylistData>>()


    fun updateUserPlaylist(useCache: Boolean) {
        if (NeteaseUser.uid != 0L) {
            val uid = NeteaseUser.uid.toString()
            GlobalScope.launch {
                HttpUtils.get("$API_MUSIC_ELEUU/user/playlist?uid=$uid"
                    , UserPlaylistData::class.java, useCache)?.apply {
                    withContext(Dispatchers.Main) {
                        userPlaylistList.value = playlist
                    }
                    if (useCache) {
                        updateUserPlaylist(false)
                    }
                }
            }
        }
    }
    fun updateLocalPalylist(){
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                localPlaylistList.value = LocalPlaylistAPI.read().lists
            }
        }
    }

}