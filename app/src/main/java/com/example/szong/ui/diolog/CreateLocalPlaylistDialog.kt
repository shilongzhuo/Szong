package com.example.szong.ui.diolog

import android.content.Context
import com.example.szong.api.music.playlist.locallist.local.LocalPlaylist
import com.example.szong.databinding.DialogCreateLocalPlaylistBinding
import com.example.szong.ui.base.BaseBottomSheetDialog
import com.example.szong.widget.toast
class CreateLocalPlaylistDialog(context: Context) : BaseBottomSheetDialog(context) {

    private var binding = DialogCreateLocalPlaylistBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.apply {
            btnConfirm.setOnClickListener {
                val name = etName.text.toString()
                val description = etDescription.text.toString()
                val imageUrl = etImageUrl.text.toString()
                if (name.isEmpty()) {
                    toast("名称不为空")
                } else {
                    LocalPlaylist.create(name, description, imageUrl)
                }
            }
        }
    }

}