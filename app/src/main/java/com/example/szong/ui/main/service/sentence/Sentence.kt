package com.example.szong.ui.main.service.sentence
import androidx.annotation.Keep
import com.example.szong.App
import com.example.szong.ui.main.service.sentence.foyoulibrary.FoyouLibrary
import com.example.szong.util.net.InternetState
import com.example.szong.util.net.MagicHttp
import com.google.gson.Gson

object Sentence {
    /**
     * 获取句子
     */
    fun getSentence(success: (SentenceData) -> Unit) {
        if (InternetState.isInternetAvailable(App.context)) { // 有网络
            when ((1..5).random()) {
                in 1..3 -> getHitokotoLibrarySentence() {
                    success.invoke(it)
                }
                else -> {
                    success.invoke(getFoyouLibrarySentence())
                }
            }
        } else { // 无网络
            success.invoke(getFoyouLibrarySentence())
        }
    }

    /**
     * 获取 Foyou 库句子
     */
    private fun getFoyouLibrarySentence(): SentenceData {
        return FoyouLibrary.getSentence()
    }

    /**
     * 获取一言库句子
     */
    private fun getHitokotoLibrarySentence(success: (SentenceData) -> Unit) {
        val url = "https://v1.hitokoto.cn/?encode=json"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val hitokotoData = Gson().fromJson(it, HitokotoData::class.java)
                val mainStr: String = hitokotoData.hitokoto?:""
                val fromWhoStr: String = hitokotoData.from_who?:""
                val fromStr: String = hitokotoData.from?:""
                success.invoke(OptimizeHitokoto.optimizeHitokoto(SentenceData(mainStr, fromWhoStr, fromStr)))
            } catch (e: Exception) {
                success.invoke(OptimizeHitokoto.optimizeHitokoto(SentenceData("", "", "")))
            }
        }, {

        })

    }

    @Keep
    data class HitokotoData(
        val hitokoto: String?,
        val from: String?,
        val from_who: String?
    )

}