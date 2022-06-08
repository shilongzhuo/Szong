package com.example.szong.ui.diolog

import android.content.Context
import com.example.szong.api.music.playlist.localplaylist.local.LocalPlaylistAPI
import com.example.szong.databinding.DialogCreateLocalPlaylistBinding
import com.example.szong.manager.activity.ActivityManager
import com.example.szong.ui.base.BaseBottomSheetDialog
import com.example.szong.widget.toast
import java.lang.Exception

class CreateLocalPlaylistDialog(context: Context) : BaseBottomSheetDialog(context) {

    private var binding = DialogCreateLocalPlaylistBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.apply {
            btnConfirm.setOnClickListener {
                val name = etName.text.toString()
                var description = etDescription.text.toString()
                var imageUrl = etImageUrl.text.toString()
                if (name.isEmpty()) {
                    toast("名称不为空")
                } else {
                    if(description.isEmpty()){
                        description="local"
                    }
                    if(imageUrl.isEmpty()){
                        imageUrl = "http://p1.music.126.net/LulOmlCSpzNQKI9xmJYIcg==/109951165395722990.jpg"
                    }
                    try {
                        LocalPlaylistAPI.create(name, description, imageUrl)
                        toast("创建歌单:$name")
                    }catch (e:Exception){
                        toast("创建歌单失败")
                    }
                    cancel()
                }
            }
        }
    }

}