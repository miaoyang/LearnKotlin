package com.ym.learnkotlin.wifip2p.util

import android.net.wifi.p2p.WifiP2pDevice




class WifiP2pUtil {
    companion object {
        fun getDeviceStatus(deviceStatus: Int): String? {
            return when (deviceStatus) {
                WifiP2pDevice.AVAILABLE -> "可用的"
                WifiP2pDevice.INVITED -> "邀请中"
                WifiP2pDevice.CONNECTED -> "已连接"
                WifiP2pDevice.FAILED -> "失败的"
                WifiP2pDevice.UNAVAILABLE -> "不可用的"
                else -> "未知"
            }
        }
    }
}