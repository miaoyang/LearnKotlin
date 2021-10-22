package com.ym.learnkotlin.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

object LogUtil {

    fun e(tag:String?,msg:String){
        Log.e(tag,msg)
    }

    fun e(tag: String?,msg: String?,e:Throwable?){
        Log.e(tag,msg,e)
    }

    fun d(tag:String?,msg:String){
        Log.e(tag,msg)
    }

    fun d(tag: String?,msg: String?,e:Throwable?){
        Log.e(tag,msg,e)
    }

    fun w(tag:String?,msg:String){
        Log.e(tag,msg)
    }

    fun w(tag: String?,msg: String?,e:Throwable?){
        Log.e(tag,msg,e)
    }
}