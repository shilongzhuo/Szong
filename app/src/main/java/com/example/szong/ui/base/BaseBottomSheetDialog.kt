package com.example.szong.ui.base

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.example.szong.R

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


abstract class BaseBottomSheetDialog(context: Context): BottomSheetDialog(context, R.style.style_default_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
        initView()
        initListener()
    }

    open fun initView() {

    }

    open fun initListener() {

    }

    override fun onStart() {
        super.onStart()
        // for landscape mode
        val behavior: BottomSheetBehavior<*> = behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}