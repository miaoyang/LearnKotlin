package com.ym.learnkotlin.wifip2p

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ym.learnkotlin.LearnKotlin
import com.ym.learnkotlin.util.LogUtil

/**
 * ref:https://github.com/leavesC/WifiP2P
 */
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
                // 用于指示 Wifi P2P 是否可用
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION ->{
                    var state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                        mWifiActionListener.wifiP2pEnabled(true)
                    }else{
                        mWifiActionListener.wifiP2pEnabled(false)
                        var wifiP2pDeviceList:List<WifiP2pDevice> = ArrayList()
                        mWifiActionListener.onPeersAvailable(wifiP2pDeviceList)
                    }
                }
                // 可用的对等点发生了改变
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION ->{
                    if (ActivityCompat.checkSelfPermission(
                            LearnKotlin.getApp(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    mWifiP2pManager.requestPeers(mChannel,WifiP2pManager.PeerListListener {
                        mWifiActionListener.onPeersAvailable(it.deviceList as List<WifiP2pDevice>)
                    })
                }
                // 表示Wi-Fi对等网络的连接状态发生了改变
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->{
                    var netWorkInfo = intent.getParcelableExtra<>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (netWorkInfo.isConnected()){
                        mWifiP2pManager.requestConnectionInfo(mChannel,WifiP2pManager.ConnectionInfoListener {
                            if (it.info!=null){
                                LogUtil.d(TAG, "Get WiFip2pInfo")
                            }else{
                                LogUtil.d(TAG,"Did't get WiFiP2pInfo")
                            }
                            mWifiActionListener.onConnectionInfoAvailable(info)
                        })
                        LogUtil.d(TAG,"Connected P2p")
                    }else{
                        mWifiActionListener.onDisconnection()
                        LogUtil.d(TAG,"Disconnected")
                    }
                }
                // 表示该设备的配置信息发生了改变
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->{
                    var info = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    if (info != null) {
                        mWifiActionListener.onSelfDeviceAvailable(info)
                    }
                }
            }
        }
    }
}