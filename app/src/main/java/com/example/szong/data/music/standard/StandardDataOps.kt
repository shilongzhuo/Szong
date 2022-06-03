package com.example.szong.data.music.standard

/**
 * 标准歌手数组转文本
 * @return 文本
 */
fun ArrayList<StandardSongData.StandardArtistData>.parse(): String {
    var artist = ""
    for (artistName in 0..this.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += this[artistName].name
    }
    return artist
}
/**
 * 标准歌手数组转文本
 * @param artistList 歌手数组
 * @return 文本
 */
fun parseArtist(artistList: ArrayList<StandardSongData.StandardArtistData>): String {
    var artist = ""
    for (artistName in 0..artistList.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += artistList[artistName].name
    }
    return artist
}
