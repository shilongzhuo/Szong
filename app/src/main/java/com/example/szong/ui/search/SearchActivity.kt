package com.example.szong.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.szong.App
import com.example.szong.App.Companion.mmkv
import com.example.szong.R
import com.example.szong.api.music.song.search.qq.QqSearchSongAPI
import com.example.szong.config.AppConfig
import com.example.szong.data.music.SearchType
import com.example.szong.data.music.standard.StandardAlbum
import com.example.szong.databinding.ActivitySearchBinding
import com.example.szong.manager.music.ApiManager
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.playlist.SongPlaylistActivity
import com.example.szong.ui.playlist.viewmodel.TAG_NETEASE
import com.example.szong.ui.search.adapter.SearchHotAdapter
import com.example.szong.ui.search.viewmodel.SearchViewModel
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.net.openUrlByBrowser
import com.example.szong.util.ui.opration.asDrawable
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.*
import com.example.szong.data.music.standard.StandardPlaylist
<<<<<<< HEAD
import com.example.szong.data.music.standard.StandardSingerData
=======
import com.example.szong.data.music.standard.StandardSinger
>>>>>>> e2d16e4d41084973f8d213438ea7a3a6851d9085
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.ui.diolog.SongMenuDialog
import com.example.szong.ui.playlist.adapter.SongAdapter
import com.example.szong.ui.search.adapter.AlbumAdapter
import com.example.szong.ui.search.adapter.PlaylistAdapter
import com.example.szong.ui.search.adapter.SingerAdapter
import com.example.szong.widget.toast

/**
 * 搜索界面
 */
class SearchActivity : BaseActivity() {

    companion object {
        private val TAG = "SearchActivity"
    }

    private lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()

    private var realKeyWord = ""

    private var searchType: SearchType

    init {
        val typeStr = mmkv.decodeString(AppConfig.SEARCH_TYPE, SearchType.SINGLE.toString())
        searchType = SearchType.valueOf(typeStr)
    }

    override fun initBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        // 获取焦点
        binding.etSearch.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        // 获取推荐关键词
        App.cloudMusicManager.getSearchDefault {
            runOnMainThread {
                // toast(it)
                binding.etSearch.hint = it.data.showKeyword
                realKeyWord = it.data.realkeyword
            }
        }
        // 获取热搜
        App.cloudMusicManager.getSearchHot {
            runOnMainThread {
                binding.rvSearchHot.layoutManager = LinearLayoutManager(this)
                val searchHotAdapter = SearchHotAdapter(it)
                searchHotAdapter.setOnItemClick(object : SearchHotAdapter.OnItemClick {
                    override fun onItemClick(view: View?, position: Int) {
                        val searchWord = it.data[position].searchWord
                        binding.etSearch.setText(searchWord)
                        binding.etSearch.setSelection(searchWord.length)
                        search()
                    }
                })
                binding.rvSearchHot.adapter = searchHotAdapter
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {
        binding.apply {
            // ivBack
            ivBack.setOnClickListener {
                if (clPanel.visibility == View.VISIBLE) {
                    finish()
                } else {
                    clPanel.visibility = View.VISIBLE
                }
            }
            // 搜索
            btnSearch.setOnClickListener { search() }

            // 网易云
            clNetease.setOnClickListener {
                changeSearchEngine(SearchViewModel.ENGINE_NETEASE)
            }
            // QQ
            clQQ.setOnClickListener {
                changeSearchEngine(SearchViewModel.ENGINE_QQ)
            }
            // 酷我
            clKuwo.setOnClickListener {
                changeSearchEngine(SearchViewModel.ENGINE_KUWO)
//                toast("酷我音源暂只支持精确搜索，需要填入完整歌曲名")
            }

<<<<<<< HEAD
            itemOpenSource.setOnClickListener {
                openUrlByBrowser(this@SearchActivity, "https://github.com/Moriafly/DsoMusic")
            }
=======
//            itemOpenSource.setOnClickListener {
//                openUrlByBrowser(this@SearchActivity, "https://github.com/Moriafly/DsoMusic")
//            }
>>>>>>> e2d16e4d41084973f8d213438ea7a3a6851d9085

            searchTypeView.setMainFabClosedDrawable(resources.getDrawable(SearchType.getIconRes(searchType)))

            searchTypeView.addActionItem(SpeedDialActionItem.Builder(R.id.search_type_single, R.drawable.ic_baseline_music_single_24).setLabel("单曲").create())
            searchTypeView.addActionItem(SpeedDialActionItem.Builder(R.id.search_type_album, R.drawable.ic_baseline_album_24).setLabel("专辑").create())
            searchTypeView.addActionItem(SpeedDialActionItem.Builder(R.id.search_type_playlist, R.drawable.ic_baseline_playlist_24).setLabel("歌单").create())
            searchTypeView.addActionItem(SpeedDialActionItem.Builder(R.id.search_type_singer, R.drawable.ic_baseline_singer_24).setLabel("歌手").create())

            searchTypeView.setOnActionSelectedListener { item ->
                searchTypeView.setMainFabClosedDrawable(item.getFabImageDrawable(this@SearchActivity))
                searchType = SearchType.getSearchType(item.id)
                mmkv.encode(AppConfig.SEARCH_TYPE, searchType.toString())
                searchTypeView.close()
                search()
                return@setOnActionSelectedListener true
            }
        }

        // 搜索框
        binding.etSearch.apply {
            setOnEditorActionListener { _, p1, _ ->
                if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
                    search()
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (binding.etSearch.text.toString() != "") {
                        binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
                    } else {
                        binding.ivClear.visibility = View.INVISIBLE // 隐藏
                    }
                }
            })
        }


        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
        }

    }

    override fun initObserver() {
        searchViewModel.searchEngine.observe(this, {
            binding.apply {
                clNetease.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
                clQQ.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
                clKuwo.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
            }
            val vis = if(it == SearchViewModel.ENGINE_NETEASE) View.VISIBLE else View.GONE
            binding.searchTypeView.visibility = vis
            when (it) {
                SearchViewModel.ENGINE_NETEASE -> {
                    binding.clNetease.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
                SearchViewModel.ENGINE_QQ -> {
                    binding.clQQ.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
                SearchViewModel.ENGINE_KUWO -> {
                    binding.clKuwo.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
            }
        })
    }

    /**
     * 搜索音乐
     */
    private fun search() {
        // 关闭软键盘
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)

        var keywords = binding.etSearch.text.toString()
        // 内部酷我
        if (keywords.startsWith("。")) {
            keywords.replace("。", "")
            searchViewModel.searchEngine.value = SearchViewModel.ENGINE_KUWO
        }
        if (keywords == "") {
            keywords = realKeyWord
            binding.etSearch.setText(keywords)
            binding.etSearch.setSelection(keywords.length)
        }
        if (keywords != "") {
            when (searchViewModel.searchEngine.value) {
                SearchViewModel.ENGINE_NETEASE -> {
                    GlobalScope.launch {
                        val result = ApiManager.searchMusic(keywords, searchType)
                        if (result != null) {
                            withContext(Dispatchers.Main) {
                                when (searchType) {
                                    SearchType.SINGLE ->  initRecycleView(result.songs)
                                    SearchType.PLAYLIST -> initPlaylist(result.playlist)
                                    SearchType.ALBUM -> initAlbums(result.albums)
                                    SearchType.SINGER -> initSingers(result.singers)
                                }
                            }
                        }
                    }
                }
                SearchViewModel.ENGINE_QQ -> {
                    QqSearchSongAPI.search(keywords) {
                        initRecycleView(it)
                    }
                }
                SearchViewModel.ENGINE_KUWO -> {
                    com.example.szong.api.music.song.search.kuwo.KuSearchSongAPI.search(keywords) {
                        initRecycleView(it)
                    }
                }
            }
            binding.clPanel.visibility = View.GONE
        }
    }

<<<<<<< HEAD
    private fun initSingers(singers: List<StandardSingerData>) {
=======
    private fun initSingers(singers: List<StandardSinger>) {
>>>>>>> e2d16e4d41084973f8d213438ea7a3a6851d9085
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = SingerAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            intent.putExtra(SongPlaylistActivity.EXTRA_TYPE, SearchType.SINGER)
            startActivity(intent)
        }.apply {
            submitList(singers)
        }
    }

    private fun initRecycleView(songList: List<StandardSongData>) {
        runOnMainThread {
            binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
            binding.rvPlaylist.adapter = SongAdapter() {
                SongMenuDialog(this, this, it) {
                    toast("不支持删除")
                }.show()
            }.apply {
                submitList(songList)
            }
        }
    }

    private fun initPlaylist(playlists:List<StandardPlaylist>) {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = PlaylistAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            startActivity(intent)
        }.apply {
            submitList(playlists)
        }
    }

    private fun initAlbums(albums:List<StandardAlbum>) {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = AlbumAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            intent.putExtra(SongPlaylistActivity.EXTRA_TYPE, SearchType.ALBUM)
            startActivity(intent)
        }.apply {
            submitList(albums)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 保存搜索引擎
        mmkv.encode(AppConfig.SEARCH_ENGINE, searchViewModel.searchEngine.value ?: SearchViewModel.ENGINE_NETEASE)

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_alpha_exit
        )
    }


    override fun onBackPressed() {
        if (binding.clPanel.visibility == View.VISIBLE) {
            super.onBackPressed()
        } else {
            binding.clPanel.visibility = View.VISIBLE
        }
    }

    /**
     * 改变搜索引擎
     */
    private fun changeSearchEngine(engineCode: Int) {
        searchViewModel.searchEngine.value = engineCode
        if (binding.clPanel.visibility != View.VISIBLE) {
            search()
        }
    }

}