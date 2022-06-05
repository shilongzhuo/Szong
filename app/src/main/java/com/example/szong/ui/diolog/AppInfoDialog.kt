package com.example.szong.ui.diolog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.example.szong.App
import com.example.szong.database.room.AppDatabase
import com.example.szong.databinding.DialogTextInfoBinding
import com.example.szong.ui.base.BaseBottomSheetDialog
import com.example.szong.util.app.defaultTypeface
import com.example.szong.util.app.getVisionCode
import com.example.szong.util.security.Secure
import com.example.szong.util.security.SkySecure

class AppInfoDialog(context: Context) : BaseBottomSheetDialog(context) {

    val binding = DialogTextInfoBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()

        binding.apply {
            tvText.typeface = defaultTypeface(App.context)
            tvText.text = """
                [app.build   ] ${getVisionCode()}
                [is debug    ] ${Secure.isDebug()}
                [database.ver] ${AppDatabase.DATABASE_VERSION}
                [model       ] ${Build.MODEL}
                [android.ver ] ${Build.VERSION.RELEASE}
                [android.sdk ] ${Build.VERSION.SDK_INT}
                [dex.crc     ] ${SkySecure.getDexCrc(App.context)}
                [name.md5    ] ${SkySecure.getMD5("com.example.szong")}
            """.trimIndent()
        }
    }

}