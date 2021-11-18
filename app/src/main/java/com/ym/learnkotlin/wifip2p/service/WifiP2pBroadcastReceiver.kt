package com.ym.learnkotlin.wifip2p.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.ym.learnkotlin.LearnKotlin
import com.ym.learnkotlin.util.LogUtil
import com.ym.learnkotlin.wifip2p.api.WifiActionListener

/**
 * ref:https://github.com/leavesC/WifiP2P
 */
class WifiP2pBroadcastReceiver(wifiP2pManager:WifiP2pManager,
                               channel:WifiP2pManager.Channel,
                               wifiActionListener: WifiActionListener
) :BroadcastReceiver() {
    companion object{
        private const val TAG:String = "WifiP2pBroadcastReceiver"

        @JvmStatic
        fun getIntentFilter():IntentFilter{
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            return intentFilter
        }

    }

    private var mWifiP2pManager:WifiP2pManager = wifiP2pManager
    private var mChannel:WifiP2pManager.Channel = channel
    private var mWifiActionListener: WifiActionListener = wifiActionListener

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.d(TAG,"Receive broadcast action = ${intent?.action}")
        if (!TextUtils.isEmpty(intent?.action)){
            when(intent?.action){
                // 用于指示 Wifi P2P 是否可用
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION ->{
                    var state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                        mWifiActionListener.wifiP2pEnabled(true)
                    }else{
                        mWifiActionListener.wifiP2pEnabled(false)
                        val wifiP2pDeviceList:List<WifiP2pDevice> = ArrayList()
                        mWifiActionListener.onPeersAvailable(wifiP2pDeviceList)
                    }
                }
                // 可用的对等点发生了改变
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION ->{
                    if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    mWifiP2pManager.requestPeers(mChannel) { peers: WifiP2pDeviceList ->
                        peers.deviceList
                        LogUtil.d(TAG, "DeviceList ${peers.deviceList}")
                    }
                }
                // 表示Wi-Fi对等网络的连接状态发生了改变
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->{
                    val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo != null) {
                        if (networkInfo.isConnected){
                            mWifiP2pManager.requestConnectionInfo(mChannel,WifiP2pManager.ConnectionInfoListener {
                                if (it!=null){
                                    LogUtil.d(TAG, "Get WiFip2pInfo")
                                }else{
                                    LogUtil.d(TAG,"Didn't get WiFiP2pInfo")
                                }
                                mWifiActionListener.onConnectionInfoAvailable(it)
                            })
                            LogUtil.d(TAG,"Connected P2p")
                        }else{
                            mWifiActionListener.onDisconnection()
                            LogUtil.d(TAG,"Disconnected")
                        }
                    }
                }
                // 表示该设备的配置信息发生了改变
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->{
                    val info = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    if (info != null) {
                        mWifiActionListener.onSelfDeviceAvailable(info)
                    }
                }
            }
        }
    }
}