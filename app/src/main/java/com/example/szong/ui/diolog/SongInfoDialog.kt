package com.example.szong.ui.diolog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.example.szong.App
import com.example.szong.R
import com.example.szong.data.music.standard.*
import com.example.szong.databinding.DialogSongInfoBinding
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.data.toSizeFormat
import com.google.android.material.bottomsheet.BottomSheetDialog

class SongInfoDialog(
    context: Context,
    private val songData: StandardSongData
) : BottomSheetDialog(context, R.style.style_default_dialog) {

    private var binding: DialogSongInfoBinding = DialogSongInfoBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            valueViewId.setValue(songData.id ?: "") // ID
            valueViewSource.setValue("未知")
            valueViewBitrate.setValue("未知")
            valueViewSize.setValue("未知")
            valueViewType.setValue("未知")
            valueViewData.setValue("未知")

            songData.let {
                when (it.source) {
                    SOURCE_NETEASE -> {
                        App.cloudMusicManager.getSongInfo(it.id ?: "") { data ->
                            runOnMainThread {
                                binding.valueViewSource.setValue("网易云音乐")

                                binding.valueViewBitrate.setValue("${data.br / 1000} kbps")
                                binding.valueViewSize.setValue(data.size.toSizeFormat())
                                binding.valueViewType.setValue(data.type ?: "未知")
                            }
                        }
                    }
                    SOURCE_QQ -> {

                       binding.valueViewSource.setValue("QQ 音乐")

                        binding.valueViewBitrate.setValue("未知")
                        binding.valueViewSize.setValue("未知")
                        binding.valueViewType.setValue("未知")
                    }
                    SOURCE_LOCAL -> {
                        valueViewSource.setValue(context.getString(R.string.local_music))
                        valueViewBitrate.setValue("未知")
                        val size = songData.localInfo?.size ?: 0L
                        valueViewSize.setValue(size.toSizeFormat())
                        valueViewType.setValue("未知")
                        valueViewData.setValue(it.localInfo?.data ?: "未知")
                    }
                    SOURCE_KUWO -> {
                        binding.valueViewSource.setValue("酷我音乐")
                    }
                }

            }

        }



    }

}