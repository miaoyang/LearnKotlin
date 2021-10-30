package com.ym.learnkotlin

import android.app.Application

class LearnKotlin:Application() {

    companion object{
        private lateinit var sInstance:LearnKotlin
        fun getApp():LearnKotlin{
            return sInstance
        }
    }

    override fun onCreate() {
        sInstance = this
        super.onCreate()
    }


}