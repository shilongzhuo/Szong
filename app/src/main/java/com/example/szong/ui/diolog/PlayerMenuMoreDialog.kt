package com.example.szong.ui.diolog

import android.content.Context
import android.content.Intent
import com.example.szong.App
import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.SOURCE_QQ
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.databinding.DialogPlayMoreBinding
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.ui.base.BaseBottomSheetDialog
import com.example.szong.ui.playlist.PlayHistoryActivity
import com.example.szong.widget.toast


class PlayerMenuMoreDialog(context: Context) : BaseBottomSheetDialog(context) {

    private val binding: DialogPlayMoreBinding = DialogPlayMoreBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }


    private var song: StandardSongData? = null

    override fun initView() {

        App.musicController.value?.getPlayingSongData()?.value?.let { it ->
            binding.tvSongName.text = it.name
            song = it
        }
    }

    override fun initListener() {
        binding.apply {
            // 添加到网易云我喜欢
            itemAddNeteaseFavorite.setOnClickListener {
                if (NeteaseUser.cookie.isEmpty()) {
                    toast("离线模式无法收藏到在线我喜欢~")
                } else {
                    song?.let {
                        when (it.source) {
                            SOURCE_NETEASE -> {
                                App.cloudMusicManager.likeSong(it.id?:"", {
                                    toast("添加到我喜欢成功")
                                    dismiss()
                                }, {
                                    toast("添加到我喜欢失败")
                                    dismiss()
                                })
                            }
                            SOURCE_QQ -> {
                                toast("暂不支持此音源")
                                dismiss()
                            }
                        }
                    }
                }
            }
            // 歌曲信息
            itemSongInfo.setOnClickListener {
                App.musicController.value?.getPlayingSongData()?.value?.let { it1 ->
                    SongInfoDialog(context, it1).show()
                }
                dismiss()
            }

            // 播放历史
            itemPlayHistory.setOnClickListener {
                it.context.startActivity(Intent(it.context, PlayHistoryActivity::class.java))
                dismiss()
            }

            timeClose.setOnClickListener {
                dismiss()
                TimingOffDialog(context).show()
            }
        }
    }

}
