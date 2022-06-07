package com.example.szong.api.music.playlist.localplaylist.local

import com.example.szong.App
import com.example.szong.data.music.standard.StandardPlaylistData
import com.example.szong.data.music.standard.StandardSongData

object LocalPlaylistAPI {

    private const val ARRAY_LOCAL_PLAYLIST = "array_local_playlist"

    // https://cj.jj20.com/2020/down.html?picurl=/up/allimg/1112/022G9140411/1Z22G40411-6.jpg
    /**
     * 读取本地歌单集合
     * @return 本地歌单集合返回
     */
    fun read(): LocalPlaylistArray {
        val defaultData = LocalPlaylistArray(ArrayList<StandardPlaylistData>())
        // MMKV 读取
        return App.mmkv.decodeParcelable(ARRAY_LOCAL_PLAYLIST
            , LocalPlaylistArray::class.java, defaultData)
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
        localData.lists.add(standardPlaylistData)
        // 保存
        save(localData)
    }

    /**
     * 保存本地歌单数据
     * 传入旧数据 [data]
     */
    fun save(data:LocalPlaylistArray) {
        App.mmkv.encode(ARRAY_LOCAL_PLAYLIST, data)
    }



}