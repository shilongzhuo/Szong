package com.example.szong.ui.main.viewmodel

import androidx.lifecycle.ViewModel


/**
 * MyFragment 的 ViewModel
 * @author Moriafly
 * @since 2021年1月21日15:23:53
 */
class MyFragmentViewModel : ViewModel() {
/**
    // 用户歌单
    var userPlaylistList = MutableLiveData<ArrayList<PlaylistData>>()

    fun updateUserPlaylist(useCache: Boolean) {
        if (User.uid != 0L) {
            val uid = User.uid.toString()
            GlobalScope.launch {
                HttpUtils.get("$API_MUSIC_ELEUU/user/playlist?uid=$uid", UserPlaylistData::class.java, useCache)?.apply {
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
*/
}