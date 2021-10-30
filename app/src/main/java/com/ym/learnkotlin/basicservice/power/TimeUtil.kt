package com.ym.learnkotlin.basicservice.power

import android.util.Log
import com.ym.learnkotlin.util.LogUtil
import java.lang.Exception
import java.sql.Date
import java.text.SimpleDateFormat

class TimeUtil {
    companion object{
        private const val TAG = "TimeUtil"
    }

    fun stringToDate(timeStr:String,parttern:String): Date? {
        var simpleDateFormat = SimpleDateFormat(parttern)
        var date: Date? =null;
        try {
            date = simpleDateFormat.parse(timeStr) as Date;
        }catch (e:Exception){
            LogUtil.e(TAG,"error is $e")
        }
        return date
    }

    fun dateToString(time:Long,parttern: String):String{
        var simpleDateFormat = SimpleDateFormat(parttern)
        var date = Date(time)
        return simpleDateFormat.format(date)
    }

}