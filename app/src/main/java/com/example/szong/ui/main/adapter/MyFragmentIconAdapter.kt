package com.example.szong.ui.main.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.szong.App
import com.example.szong.R
import com.example.szong.manager.activity.ActivityManager.startLocalMusicActivity
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.ui.playlist.PlayHistoryActivity
import com.example.szong.ui.playlist.SongPlaylistActivity
import com.example.szong.ui.playlist.viewmodel.TAG_LOCAL_MY_FAVORITE
import com.example.szong.ui.user.UserCloudActivity
import com.example.szong.util.net.status.ErrorCode
import com.example.szong.util.ui.animation.AnimationUtil
import com.example.szong.widget.toast

class MyFragmentIconAdapter(val context: Context): RecyclerView.Adapter<MyFragmentIconAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clLocal: ConstraintLayout = view.findViewById(R.id.clLocal)
        val clUserCloud: ConstraintLayout = view.findViewById(R.id.clUserCloud)
        val clFavorite: ConstraintLayout = view.findViewById(R.id.clFavorite)
<<<<<<< HEAD
        val clPersonalFM: ConstraintLayout = view.findViewById(R.id.clPersonalFM)
=======
>>>>>>> e2d16e4d41084973f8d213438ea7a3a6851d9085
        val clLatest: ConstraintLayout = view.findViewById(R.id.clLatest)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_fragment_my_icon, parent, false).apply {
            return ViewHolder(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
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
<<<<<<< HEAD
            clPersonalFM.setOnClickListener {
                AnimationUtil.click(it)
                if (NeteaseUser.hasCookie) {
                    if (App.musicController.value?.personFM?.value != true) {
                        App.musicController.value?.setPersonFM(true)
                        App.activityManager.startPlayerActivity(context as Activity)
                    } else {
                        App.activityManager.startPlayerActivity(context as Activity)
                    }
                } else {
                   toast(ErrorCode.getMessage(ErrorCode.ERROR_NOT_COOKIE))
                }
            }
=======

>>>>>>> e2d16e4d41084973f8d213438ea7a3a6851d9085
            // 用户云盘
            clUserCloud.setOnClickListener {
                AnimationUtil.click(it)
                if (NeteaseUser.hasCookie) {
                    context.startActivity(Intent(context, UserCloudActivity::class.java))
                } else {
                    toast(ErrorCode.getMessage(ErrorCode.ERROR_NOT_COOKIE))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

}