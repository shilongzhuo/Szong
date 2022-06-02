package com.example.szong.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.szong.R

class MyFragmentIconAdapter(val context: Context): RecyclerView.Adapter<MyFragmentIconAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clLocal: ConstraintLayout = view.findViewById(R.id.clLocal)
        val clUserCloud: ConstraintLayout = view.findViewById(R.id.clUserCloud)
        val clFavorite: ConstraintLayout = view.findViewById(R.id.clFavorite)
        val clPersonalFM: ConstraintLayout = view.findViewById(R.id.clPersonalFM)
        val clLatest: ConstraintLayout = view.findViewById(R.id.clLatest)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_fragment_my_icon, parent, false).apply {
            return ViewHolder(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       /** holder.apply {
            clLocal.setOnClickListener {
                AnimationUtil.click(it)
                 startLocalMusicActivity(context)
            }
            // 我喜欢的音乐
            clFavorite.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(context, SongPlaylistActivity::class.java).apply {
                    putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_LOCAL_MY_FAVORITE)
//                    putExtra(PlaylistActivity2.EXTRA_LONG_PLAYLIST_ID, 0L)
//                    putExtra(PlaylistActivity2.EXTRA_INT_TAG, PLAYLIST_TAG_MY_FAVORITE)
                }
                context.startActivity(intent)
            }
            // 播放历史
            clLatest.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(context, PlayHistoryActivity::class.java)
                context.startActivity(intent)
            }
            clPersonalFM.setOnClickListener {
                AnimationUtil.click(it)
                if (User.hasCookie) {
                    if (App.musicController.value?.personFM?.value != true) {
                        App.musicController.value?.setPersonFM(true)
                        App.activityManager.startPlayerActivity(context as Activity)
                    } else {
                        App.activityManager.startPlayerActivity(context as Activity)
                    }
                } else {
                    ErrorCode.toast(ErrorCode.ERROR_NOT_COOKIE)
                }
            }
            // 用户云盘
            clUserCloud.setOnClickListener {
                AnimationUtil.click(it)
                if (User.hasCookie) {
                    context.startActivity(Intent(context, UserCloudActivity::class.java))
                } else {
                    ErrorCode.toast(ErrorCode.ERROR_NOT_COOKIE)
                }
            }
        }*/
    }

    override fun getItemCount(): Int {
        return 1
    }

}