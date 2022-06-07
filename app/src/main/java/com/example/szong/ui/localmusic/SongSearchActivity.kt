package com.example.szong.ui.localmusic

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.data.music.standard.parse
import com.example.szong.databinding.ActivitySongSearchBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.diolog.SongMenuDialog
import com.example.szong.ui.playlist.adapter.SongAdapter
import com.example.szong.widget.toast
import java.util.*

/**
 * 本地搜索
 */
class SongSearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySongSearchBinding

    val adapter = SongAdapter() {
        SongMenuDialog(this, this, it) {
            toast("不支持删除")
        }.show()
    }

    override fun initBinding() {
        binding = ActivitySongSearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {

        with(binding) {
            // adapter.submitList(SongSearchTransmit.songList)
            rvSongList.layoutManager = LinearLayoutManager(this@SongSearchActivity)
            rvSongList.adapter = adapter
        }
    }

    override fun initListener() {
        binding.etSearch.apply {
            setOnEditorActionListener { _, p1, _ ->
//                if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
//
//                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    search()
//                    if (binding.etSearch.text.toString() != "") {
//                        binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
//                    } else {
//                        binding.ivClear.visibility = View.INVISIBLE // 隐藏
//                    }
                }
            })
        }
    }

    private fun search() {
        val keywords = binding.etSearch.text.toString()
        if (keywords.isEmpty()) {
            return
        }
        val keyArrayList = ArrayList<StandardSongData>()
        SongSearchTransmit.songList.forEach {
            val key = it.name + it.artists?.parse()
            if (keywords in key) {
                keyArrayList.add(it)
            }
        }
        adapter.submitList(keyArrayList)
    }

}