package com.example.szong.widget

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.szong.R

class TitleBarLayout(context: Context, attrs: AttributeSet): androidx.constraintlayout.widget.ConstraintLayout(
    context,
    attrs
) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarLayout)
    val text = typedArray.getString(R.styleable.TitleBarLayout_text)

    private var tvTitleBar: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.szongx_titlebar_layout, this)

        tvTitleBar = findViewById(R.id.tvTitleBar)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        tvTitleBar.text = text

        btnBack.setOnClickListener {
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(it, "alpha", 0F, 1.0F)
            objectAnimator.duration = 1000
            objectAnimator.start()

            val activity = context as Activity
            activity.finish()
        }
    }

    fun setTitleBarText(text: String) {
        tvTitleBar.text = text
    }

}