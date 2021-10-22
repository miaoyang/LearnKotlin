package com.ym.learnkotlin.design.buildpattern

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Computer(
    @SerializedName("cpu")
    private var mCpu:String? = null,

    @SerializedName("mainboard")
    private var mMainboard:String? = null,

    @SerializedName("ram")
    private var mRam:String? = null
):Serializable, Parcelable {
    companion object{
        private const val serialVersionUID = 2021101202106L
    }

}
