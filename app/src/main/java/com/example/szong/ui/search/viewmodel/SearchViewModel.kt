package com.example.szong.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.szong.App
import com.example.szong.config.AppConfig

/**
 * 搜索 ViewModel
 */
class SearchViewModel: ViewModel() {

    companion object {
        const val ENGINE_NETEASE = 1
        const val ENGINE_QQ = 2
        const val ENGINE_KUWO = 3
    }

    /* 搜索引擎 */
    var searchEngine = MutableLiveData(App.mmkv.decodeInt(AppConfig.SEARCH_ENGINE, ENGINE_NETEASE))

}