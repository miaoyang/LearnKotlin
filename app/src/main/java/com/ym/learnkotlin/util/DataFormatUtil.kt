package com.ym.learnkotlin.util

import java.text.SimpleDateFormat
import java.util.*

object DataFormatUtil {
    val TAG:String = "DataFormateUtil"

    var currentTime:String = ""
        get(){
            val dfs = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return dfs.format(Date())
        }

    /**
     * fomate time
     */
    fun formatDate(time:Long):String{
        val duration = System.currentTimeMillis()
        LogUtil.e(TAG,"time:$time")
        return when{
            duration < 60*1000 -> "${duration / 1000} second ago"
            duration < 60*60*1000 -> "${duration / 1000 / 60} minutes ago"
            duration < 24*60*1000 -> "${duration / 1000 / 60 / 24} hour ago"
            else ->{
                val dfs = SimpleDateFormat("yyyy-MM-dd")
                val date = dfs.format(Date(time))
                date
            }
        }
    }
}