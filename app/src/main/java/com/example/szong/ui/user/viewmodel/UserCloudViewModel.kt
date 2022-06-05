package com.example.szong.ui.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.szong.api.user.auth.netease.UserCloudAPI
import com.example.szong.api.user.auth.netease.toStandard
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.data.toSizeFormat
import com.example.szong.util.net.status.ErrorCode
import com.example.szong.widget.toast

class UserCloudViewModel : ViewModel() {

    var songlist = MutableLiveData<ArrayList<StandardSongData>>().also {
        it.value = ArrayList()
    }

    var size = MutableLiveData<String>().also {
        it.value = ""
    }

    private var offset = 0
    private var isLoading = false // 是否正在加载
    private var isFinish = false

    /**
     * 获取数据
     */
    fun fetchData() {
        if (isLoading || isFinish) {
            return
        }
        isLoading = true
        // request data
        UserCloudAPI.getUserCloud(offset, {
            runOnMainThread {
                if (it.hasMore) {
                    offset += 50
                } else {
                    isFinish = true
                }
                if (size.value.isNullOrEmpty()) {
                    size.value = "${(it.size.toLongOrNull() ?: 0L).toSizeFormat()} / ${(it.maxSize.toLongOrNull() ?: 0L).toSizeFormat()}"
                }
                songlist.value = arrayListOf(
                    songlist.value!!.toList(),
                    it.data.toStandard().toList()
                ).flatten() as ArrayList<StandardSongData>
            }
            isLoading = false
        }, {
            toast(ErrorCode.getMessage(it))
        })
    }

}