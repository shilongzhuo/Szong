package com.example.szong.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.szong.R

class BlankAdapter(private val bottomHeight: Int): RecyclerView.Adapter<BlankAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clBase: ConstraintLayout = view.findViewById(R.id.clBase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_blank, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            (clBase.layoutParams as RecyclerView.LayoutParams).apply {
                height = bottomHeight
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

}