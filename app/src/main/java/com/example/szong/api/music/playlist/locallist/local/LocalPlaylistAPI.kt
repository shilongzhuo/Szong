package com.example.szong.api.music.playlist.locallist.local

import com.example.szong.App
import com.example.szong.data.music.standard.StandardPlaylistData
import com.example.szong.data.music.standard.StandardSongData

object LocalPlaylistAPI {

    private const val ARRAY_LOCAL_PLAYLIST = "array_local_playlist"

    /**
     * 读取本地歌单集合
     * @return 本地歌单集合返回
     */
    private fun read(): LocalPlaylistArrayData {
        val defaultData = LocalPlaylistArrayData(ArrayList())
        // MMKV 读取
        return App.mmkv.decodeParcelable(ARRAY_LOCAL_PLAYLIST, LocalPlaylistArrayData::class.java, defaultData)
    }

    /**
     * 创建一个本地歌单
     * 传入 [name] 歌单名称，[description] 歌单描述，[imageUrl] 歌单封面链接
     */
    fun create(name: String, description: String, imageUrl: String) {
        // 空歌曲列表
        val emptyArrayList = ArrayList<StandardSongData>()
        // 创建空歌单
        val standardPlaylistData = StandardPlaylistData(name, description, imageUrl, emptyArrayList)
        // 读取本地集合
        val localData = read()
        localData.data.add(standardPlaylistData)
        // 保存
        save(localData)
    }

    /**
     * 保存本地歌单数据
     * 传入旧数据 [oldData]
     */
    fun save(oldData: LocalPlaylistArrayData) {
        App.mmkv.encode(ARRAY_LOCAL_PLAYLIST, oldData)
    }

}