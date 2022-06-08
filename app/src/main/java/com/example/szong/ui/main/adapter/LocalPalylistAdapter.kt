package com.example.szong.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.example.szong.App
import com.example.szong.R
import com.example.szong.data.music.standard.StandardPlaylistData
import com.example.szong.util.ui.opration.asDrawable
import com.example.szong.util.ui.opration.dp

/**
 * 我的歌单适配器
 */
class LocalPalylistAdapter
    (private val itemClickListener: (StandardPlaylistData) -> Unit)
    : ListAdapter<StandardPlaylistData, LocalPalylistAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemClickListener: (StandardPlaylistData) -> Unit) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)

        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)

        var selectPlaylist: StandardPlaylistData? = null

        init {
            clTrack.setOnClickListener {
                selectPlaylist?.let { it1 -> itemClickListener(it1)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val playlist = getItem(position)
            selectPlaylist = playlist

            if (position == 0) {
                clTrack.updateLayoutParams<RecyclerView.LayoutParams> {
                   height = (68 + 16).dp()
                }
                clTrack.background = R.drawable.bg_card_item_top.asDrawable(clTrack.context)
            } else {
                clTrack.updateLayoutParams<RecyclerView.LayoutParams> {
                    height = (68).dp()
                }
                clTrack.background = R.drawable.bg_card_item.asDrawable(clTrack.context)
            }

            val url = App.cloudMusicManager.getPicture(playlist.picUrl, 56.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = playlist.name
            tvTrackCount.text = "本地歌单"
                //holder.itemView.context.getString(R.string.songs, playlist.songs.size)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardPlaylistData>() {
        override fun areItemsTheSame(oldItem: StandardPlaylistData, newItem: StandardPlaylistData): Boolean {
            return oldItem.picUrl == newItem.picUrl && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: StandardPlaylistData, newItem: StandardPlaylistData): Boolean {
            return oldItem == newItem
        }
    }

}
