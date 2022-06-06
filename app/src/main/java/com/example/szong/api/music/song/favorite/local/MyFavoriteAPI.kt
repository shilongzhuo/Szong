package com.example.szong.api.music.song.favorite.local

import com.example.szong.App
import com.example.szong.database.room.MyFavoriteData
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.widget.toast
import org.jetbrains.annotations.TestOnly
import kotlin.concurrent.thread

/**
 * 本地我喜欢的
 */
object MyFavoriteAPI {

    private val myFavoriteDao = App.appDatabase.myFavoriteDao()

    /**
     * 读取本地歌曲
     */
    @TestOnly
    fun read(success: (ArrayList<StandardSongData>) -> Unit) {
        thread {
            val data = ArrayList<StandardSongData>()
            for (myFavorite in myFavoriteDao.loadAll()) {
                data.add(0, myFavorite.songData)
            }
            success.invoke(data)
        }
    }

    /**
     * 添加一首歌
     */
    @TestOnly
    fun addSong(songData: StandardSongData) {
        thread {
            val myFavoriteData = MyFavoriteData(songData)
            if (myFavoriteData !in myFavoriteDao.loadAll()) {
                myFavoriteDao.insert(myFavoriteData)
                toast("添加成功~")
            } else {
                toast("已经添加过了哦~")
            }
        }
    }

    /**
     * 通过 id 删除一首歌
     */
    fun deleteById(id: String) {
        thread {
            myFavoriteDao.deleteById(id)
        }
    }

    /**
     * 判断歌曲是否存在数据库
     */
    fun isExist(songData: StandardSongData, exist: (Boolean) -> Unit) {
        thread {
            val myFavoriteData = MyFavoriteData(songData)
            if (myFavoriteData in myFavoriteDao.loadAll()) {
                exist.invoke(true)
            } else {
                exist.invoke(false)
            }
        }
    }

}
