package com.example.szong.api.music.song.url.qq

import com.example.szong.util.app.loge
import com.example.szong.util.net.HttpUtils
import org.jetbrains.annotations.TestOnly

object PlayUrl {

    suspend fun getPlayUrl(songmid: String): String {
        if (getSongUrl(songmid) != "") {
            return getSongUrl(songmid)
        } else {
            // 地址
            val url = """https://u.y.qq.com/cgi-bin/musicu.fcg?g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&data={"req":{"module":"CDN.SrfCdnDispatchServer","method":"GetCdnDispatch","param":{"guid":"8348972662","calltype":0,"userip":""}},"req_0":{"module":"vkey.GetVkeyServer","method":"CgiGetVkey","param":{"guid":"8348972662","songmid":["${songmid}"],"songtype":[1],"uin":"0","loginflag":1,"platform":"20"}},"comm":{"uin":0,"format":"json","ct":24,"cv":0}}""".trimIndent()
            loge("请求地址：${url}")

            HttpUtils.get(url,VkeyData::class.java)?.apply {
                val ip = req.data.freeflowsip[0]
                // 获取 vkey
                val purl = req_0.data.midurlinfo[0].purl
                if (purl != "") {
                    return ip + purl
                }
            }
            return getSongUrl(songmid)
        }


    }
}
    @TestOnly
    fun getSongUrl(id: String): String {
        return when (id) {
            "812400", "001BaZ263wqkmP" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoZ1FDYVZIbHNPTkZHbnVMP2U9Z0VVZzhU.mp3" // ラムジ - PLANET
            "1487943004", "0001r55f1SLVWd" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDNnNHQ4czVHTDJKTVluP2U9WU5EMUJm.mp3" // 幸存者 - 林俊杰
            "002eqWNw0eVgZg" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDRhajdsRHkxcHJEd21aP2U9QkJ2bjhq.mp3" // TORTOISE KNIGHT - 岩崎太整 / 二宮愛
            "0039MnYb0qxYhV" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDg2aHV1S2JORDVYcFFZP2U9OVpYWVhn.mp3" // 晴天 - 周杰伦
            "003aAYrm3GE0Ac" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoZ0plWXc3YTFka0hybVppP2U9Z05TbGk0.mp3" // 稻香 - 周杰伦
            "394990", "000KDPfR0FMjQR" -> "http://ar.h5.ra01.sycdn.kuwo.cn/693743c6d4b60c9b7c116c9f12f1601e/5fcf6b05/resource/n2/128/74/39/3515315661.mp3" // 左手右手 - 杨沛宜
            else -> ""
        }
    }

    data class VkeyData(
        val req: ReqData,
        val req_0: Req_0Data
    )

    data class ReqData(
        val data: Freeflowsip
    )

    data class Freeflowsip(
        val freeflowsip: ArrayList<String>
    )

    data class Req_0Data(
        val data: Midurlinfo
    )

    data class Midurlinfo(
        val midurlinfo: ArrayList<VkeyReqData>
    )

    data class VkeyReqData(
        val purl: String,
        val vkey: String
    )

