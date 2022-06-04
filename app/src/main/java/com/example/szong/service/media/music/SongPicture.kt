package com.example.szong.service.media.music

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.example.szong.R
import com.example.szong.config.API_FCZBL_VIP
import com.example.szong.data.music.standard.*
import com.example.szong.api.music.song.url.local.LocalMusic
import com.example.szong.util.app.loge
import com.example.szong.util.image.CoilUtil
import com.example.szong.util.ui.opration.dp


import org.jetbrains.annotations.TestOnly

/**
 * 测试
 */
object SongPicture {

    /**
     * 获取 PlayerActivityCover 图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @TestOnly
    fun getPlayerActivityCoverBitmap(context: Context, songData: StandardSongData, size: Int, success: (Bitmap) -> Unit) {
        // 当歌词封面为空
        if (songData.imageUrl == null) {
            ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.let { it1 ->
                success.invoke(it1.toBitmap(128.dp(), 128.dp()))
            }
        }
        when (songData.source) {
            SOURCE_NETEASE -> {
                if (songData.imageUrl != null) {
                    val url = if (songData.imageUrl == "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                        || songData.imageUrl == "https://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                    ) {
                        "$API_FCZBL_VIP/?type=cover&id=${songData.id}"
                    } else {
                        "${songData.imageUrl}?param=${size}y${size}"
                    }
                    CoilUtil.load(url) {
                        success.invoke(it)
                    }
                }
            }
            SOURCE_QQ -> {
                val imageUrl = songData.imageUrl
                // val url = "https://y.gtimg.cn/music/photo_new/T002R${size}x${size}M000${songData.imageUrl}.jpg?max_age=2592000"
                val url = if (imageUrl != null && imageUrl.contains("music.126.net")) {
                    imageUrl
                }  else {"https://y.gtimg.cn/music/photo_new/T002R300x300M000${songData.imageUrl}.jpg?max_age=2592000"}
                loge("getPlayerActivityCoverBitmapQQ图片url【${url}】")
                CoilUtil.load(context, url) {
                    success.invoke(it)
                }
            }
            SOURCE_KUWO -> {
                songData.imageUrl?.let { url ->
                    CoilUtil.load(context, url) {
                        success.invoke(it)
                    }
                }
            }
            SOURCE_LOCAL -> {
                songData.imageUrl?.let {
                    val bitmap = LocalMusic.getBitmapFromUir(context, it.toUri())
                    if (bitmap != null) {
                        success.invoke(bitmap)
                    }
                }
            }
            else -> {
                val commonBitmap: Bitmap? = ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.toBitmap(128.dp(), 128.dp())
                if (commonBitmap != null) {
                    success.invoke(commonBitmap)
                }
            }
        }

    }

}