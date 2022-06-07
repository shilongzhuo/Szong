package com.example.szong.api.music.playlist.localplaylist.local

import android.os.Parcelable
import com.example.szong.data.music.standard.StandardPlaylistData

import kotlinx.parcelize.Parcelize

/**
 * 本地歌单集合类
 */
@Parcelize
data class LocalPlaylistArray(
    // 数据
    val lists: ArrayList<StandardPlaylistData>
): Parcelable