package com.example.szong.data.music.standard


/**
 * 歌曲列表
 */
data class StandardPlaylist(
    val id:Long,
    val name:String,
    val coverImgUrl:String,
    val description:String,
    val authorName:String,
    val trackCount:Int,//曲目数
    val playCount:Long
)