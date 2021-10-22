package com.ym.learnkotlin.wifip2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.text.TextUtils
import com.ym.learnkotlin.util.LogUtil

class WifiP2pBroadcastReceiver(wifiP2pManager:WifiP2pManager,
                               channel:WifiP2pManager.Channel,
                               wifiActionListener:WifiActionListener) :BroadcastReceiver() {
    companion object{
        private const val TAG:String = "WifiP2pBroadcastReceiver"

        fun getIntentFilter():IntentFilter{
            var intentFilter = IntentFilter()
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            return intentFilter
        }

    }

    private var mWifiP2pManager:WifiP2pManager
    private var mChannel:WifiP2pManager.Channel
    private var mWifiActionListener:WifiActionListener

    init {
        mWifiP2pManager = wifiP2pManager
        mChannel = channel
        mWifiActionListener = wifiActionListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.d(TAG,"Receive broadcast action = ${intent?.action}")
        if (!TextUtils.isEmpty(intent?.action)){
            when(intent?.action){

            }
        }
    }
}