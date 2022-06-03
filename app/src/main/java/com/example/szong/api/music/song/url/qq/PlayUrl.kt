package com.example.szong.api.music.song.url.qq

import com.example.szong.util.app.loge
import com.example.szong.util.net.HttpUtils

object PlayUrl {

    suspend fun getPlayUrl(songmid: String) :String {
            // 地址
            val url =
                """https://u.y.qq.com/cgi-bin/musicu.fcg?g_tk=5381&loginUin=0
                    &hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&no
                    tice=0&platform=yqq.json&needNewCode=0&data={"req":{"modu
                    le":"CDN.SrfCdnDispatchServer","method":"GetCdnDispatch",
                    "param":{"guid":"8348972662","calltype":0,"userip":""}},"
                    req_0":{"module":"vkey.GetVkeyServer","method":"CgiGetVkey
                    ","param":{"guid":"8348972662","songmid":["${songmid}"],"s
                    ongtype":[1],"uin":"0","loginflag":1,"platform":"20"}},"co
                    mm":{"uin":0,"format":"json","ct":24,"cv":0}}""".trimIndent()
            loge("请求地址：${url}")

         var result=HttpUtils.get(url, VkeyData::class.java)?.let {
                val ip = it.req.data.freeflowsip[0]
                // 获取 vkey
                val purl = it.req_0.data.midurlinfo[0].purl
               ip + purl
            }
        return result!!
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

