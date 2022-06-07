package com.example.szong.ui.diolog

import android.app.Activity
import android.content.Context
import com.example.szong.App
import com.example.szong.api.music.song.favorite.local.MyFavoriteAPI
import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.databinding.DialogSongMenuBinding
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.plugin.PluginConstants
import com.example.szong.plugin.PluginSupport
import com.example.szong.ui.base.BaseBottomSheetDialog
import com.example.szong.widget.toast

/**
 * 每个歌曲右边三个点点击后显示
 */
class SongMenuDialog
constructor(
    context: Context,
    private val activity: Activity,
    private val songData: StandardSongData,
    private val itemDeleteListener: () -> Unit
) : BaseBottomSheetDialog(context) {

    private var binding = DialogSongMenuBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
        super.initListener()
        binding.apply {
            // 下一首播放
            itemNextPlay.setOnClickListener {
                App.musicController.value?.addToNextPlay(songData)
                toast("成功添加到下一首播放")
                dismiss()
            }
            // 添加到本地我喜欢
            itemAddLocalMyFavorite.setOnClickListener {
                MyFavoriteAPI.addSong(songData)
                dismiss()
            }
            //添加到本地歌单
            itemAddLocalMyPlaylist.setOnClickListener{

            }
            // 添加到网易云我喜欢
//            itemAddNeteaseFavorite.setOnClickListener {
//                if (NeteaseUser.cookie.isEmpty()) {
//                    toast("离线模式无法收藏到在线我喜欢~")
//                } else {
//                    when (songData.source) {
//                        SOURCE_NETEASE -> {
//                            App.cloudMusicManager.likeSong(songData.id?:"", {
//                                toast("添加到我喜欢成功")
//                            }, {
//                                toast("添加到我喜欢失败")
//                            })
//                        }
//                        else -> {
//                            toast("暂不支持此音源")
//                            dismiss()
//                        }
//                    }
//                }
//            }

            // 歌曲信息
            itemSongInfo.setOnClickListener {
                // toast("歌曲信息 ${ songData.id }")
                SongInfoDialog(context, songData).show()
                // 自己消失
                dismiss()
            }

            // 歌曲评论
            itemSongComment.setOnClickListener {
                App.activityManager.startCommentActivity(activity, songData.source?: SOURCE_NETEASE, songData.id?:"")
                dismiss()
            }
            // 歌曲删除
            itemDeleteSong.setOnClickListener {
                itemDeleteListener()
                dismiss()
            }

            PluginSupport.setSong(songData);
            PluginSupport.setActivity(activity)
            PluginSupport.setDialog(this@SongMenuDialog)
            PluginSupport.setMenu(songMenuParent)
            PluginSupport.apply(PluginConstants.POINT_SONG_MENU_DIALOG_INIT)
        }
    }

}