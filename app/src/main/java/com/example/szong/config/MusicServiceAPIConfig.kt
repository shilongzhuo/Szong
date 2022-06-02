package com.example.szong.config


/**
 * 默认 API
 * 网易云音乐 API
 */
const val API_DEFAULT = "https://cloudmusic.moriafly.xyz"

/**
 * 备用API
 * 网易云音乐 API
 */
const val API_SZONG = "https://netease-cloud-music-api-lemon.vercel.app"

/**
 * @名称 音乐 网易云 api
 * @限制 暂无
 * @测试 主要 api，绝大多数稳定
 * @类型 适用 NodeJs 版
 */
const val API_MUSIC_API = "http://musicapi.leanapp.cn"

/**
 * @名称 网易云简单 api
 * @限制 暂无，很快，稳定
 * @测试 用于歌词，很简单
 * @获取歌词 https://api.fczbl.vip/163/?type=lrc&id=1438470159 // 传入歌曲 id
 *
 */
const val API_FCZBL_VIP = "https://api.fczbl.vip/163"


// https://api.imjad.cn/cloudmusic/?type=song&id=545505696&br=999000
const val API_IMJAD = "https://api.imjad.cn/cloudmusic"

/**
 * 网易云官方 api
 */
const val API_NETEASE = "https://music.163.com/api"

/**
 * @类型 适用 NodeJs 版
 * @例子 http://music.eleuu.com/banner?type=1
 */
const val API_MUSIC_ELEUU = "http://music.eleuu.com"

const val API_AUTU = "https://autumnfish.cn"

const val API_LOGIN = API_AUTU

object CloudMusicApi {

    /**
     * 默认搜索关键词
     * 说明 : 调用此接口 , 可获取默认搜索关键词
     */
    const val SEARCH_DEFAULT = "${API_AUTU}/search/default"

    /**
     * 热搜列表(详细)
     * 说明 : 调用此接口,可获取热门搜索列表
     */
    const val SEARCH_HOT_DETAIL = "${API_AUTU}/search/hot/detail"

    /**
     * 获取歌手部分信息和歌手单曲
     * 说明 : 调用此接口 , 传入歌手 id, 可获得歌手部分信息和热门歌曲
     * 必选参数 : id: 歌手 id, 可由搜索接口获得
     * 接口地址 : /artists
     * 调用例子 : /artists?id=6452
     */
    const val ARTISTS = "${API_AUTU}/artists"

    /**
     * 获取歌词
     * 说明 : 调用此接口 , 传入音乐 id 可获得对应音乐的歌词 ( 不需要登录 )
     * 必选参数 : id: 音乐 id
     * 接口地址 : /lyric
     * 调用例子 : /lyric?id=33894312
     * 返回数据如下图 : 获取歌词
     */
    const val LYRIC = "${API_AUTU}/lyric"

    /**
     * 获取用户歌单
     * 说明 : 登录后调用此接口 , 传入用户 id, 可以获取用户歌单
     * 必选参数 : uid : 用户 id
     * 可选参数 :
     * limit : 返回数量 , 默认为 30
     * offset : 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * 接口地址 : /user/playlist
     * 调用例子 : /user/playlist?uid=32953014
     */
    const val USER_PLAYLIST = "${API_MUSIC_ELEUU}/user/playlist"

    /**
     * 推荐新音乐
     * 说明 : 调用此接口 , 可获取推荐新音乐
     * 可选参数 : limit: 取出数量 , 默认为 10 (不支持 offset)
     * 接口地址 : /personalized/newsong
     * 调用例子 : /personalized/newsong
     */
    const val PERSONALIZED_NEW_SONG = "${API_MUSIC_ELEUU}/personalized/newsong"

}


