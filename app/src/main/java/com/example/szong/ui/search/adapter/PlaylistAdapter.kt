package com.example.szong.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.example.szong.App
import com.example.szong.R
import com.example.szong.data.music.standard.StandardPlaylist
import com.example.szong.util.ui.opration.dp

class PlaylistAdapter (private val itemClickListener: (StandardPlaylist) -> Unit)
    : ListAdapter<StandardPlaylist, PlaylistAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemClickListener: (StandardPlaylist) -> Unit) : RecyclerView.ViewHolder(view) {
        private val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tips: TextView = view.findViewById(R.id.tips)
        val description: TextView = view.findViewById(R.id.description)

        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)

        var selectPlaylist: StandardPlaylist? = null

        init {
            clTrack.setOnClickListener {
                selectPlaylist?.let { it1 -> itemClickListener(it1)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_playlist_new, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val playlist = getItem(position)
            selectPlaylist = playlist

            val url = App.cloudMusicManager.getPicture(playlist.coverImgUrl, 80.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = playlist.name
            val context = holder.itemView.context
            description.text = playlist.description
            "${playlist.authorName}??????${playlist.trackCount}??????${playlist.playCount}?????????".also { tips.text = it }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardPlaylist>() {
        override fun areItemsTheSame(oldItem: StandardPlaylist, newItem: StandardPlaylist): Boolean {
            return oldItem.coverImgUrl == newItem.coverImgUrl && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StandardPlaylist, newItem: StandardPlaylist): Boolean {
            return oldItem == newItem
        }
    }

}