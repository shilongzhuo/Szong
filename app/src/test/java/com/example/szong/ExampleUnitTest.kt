package com.example.szong

import com.example.szong.util.data.md5
import com.example.szong.util.security.SkySecure.appMd5
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun appMd5Equ(args:Array<String>){
        assertEquals(appMd5,"Szong".md5())
    }
}