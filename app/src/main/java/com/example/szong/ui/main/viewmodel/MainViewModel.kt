package com.example.szong.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.szong.App
import com.example.szong.config.AppConfig
import com.example.szong.manager.user.NeteaseUser


/**
 * MainActivity ViewModel
 * 首先LiveData其实与数据实体类(POJO类)是一样的东西,它负责暂存数据.
 * 其次LiveData其实也是一个观察者模式的数据实体类,它可以跟它注册的观察者回调数据是否已经更新.
 */
class MainViewModel: ViewModel() {

    /** 状态栏高度 */
    val statusBarHeight = MutableLiveData<Int>()

    /** 导航栏高度 */
    val navigationBarHeight = MutableLiveData<Int>()

    /**
     * 用户 id
     */
    val userId =  MutableLiveData<Long>().also {
        it.value = NeteaseUser.uid
    }

    /**
     * 网易登录后才可见
     */
    val neteaseLiveVisibility = MutableLiveData<Boolean>().also {
        it.value = App.mmkv.decodeBool(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
    }

    /**
     * 句子推荐可见性
     */
    var sentenceVisibility = MutableLiveData<Boolean>().also {
        it.value = App.mmkv.decodeBool(AppConfig.SENTENCE_RECOMMEND, true)
    }

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = NeteaseUser.uid
    }

    /**
     * 刷新 UI
     */
    fun updateUI() {
        neteaseLiveVisibility.value = App.mmkv.decodeBool(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
        sentenceVisibility.value = App.mmkv.decodeBool(AppConfig.SENTENCE_RECOMMEND, true)
    }

}