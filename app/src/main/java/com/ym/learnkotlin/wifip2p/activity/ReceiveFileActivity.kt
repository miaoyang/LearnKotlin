package com.ym.learnkotlin.wifip2p.activity

import android.content.BroadcastReceiver
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.ym.learnkotlin.R
import com.ym.learnkotlin.util.LogUtil
import com.ym.learnkotlin.wifip2p.*
import com.ym.learnkotlin.wifip2p.api.WifiActionListener
import com.ym.learnkotlin.wifip2p.service.WifiServerService.WifiServerBinder
import com.ym.learnkotlin.wifip2p.service.WifiP2pBroadcastReceiver
import com.ym.learnkotlin.wifip2p.service.WifiServerService
import java.io.File


class ReceiveFileActivity : BaseActivity() {
    companion object{
        private const val TAG = "ReceiveFileActivity"
    }

    private lateinit var ivImage:ImageView
    private lateinit var tvLog:TextView
    private lateinit var processDialog:ProgressDialog
    private lateinit var wifiP2pManager:WifiP2pManager
    private lateinit var channel:WifiP2pManager.Channel
    private var connectionInfoAvailable:Boolean = false
    private lateinit var broadcastReceiver:BroadcastReceiver
    private lateinit var wifiServerService: WifiServerService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_file)
        initView()
        wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        if (null == wifiP2pManager){
            finish()
            return
        }
        channel = wifiP2pManager.initialize(this, Looper.getMainLooper(),actionListener)
        broadcastReceiver = WifiP2pBroadcastReceiver(wifiP2pManager,channel,actionListener)
        registerReceiver(broadcastReceiver, WifiP2pBroadcastReceiver.getIntentFilter())
        bindService()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != wifiServerService){
            wifiServerService.setProgressChangListener(null)
            unbindService(serviceConnection)
        }
        unregisterReceiver(broadcastReceiver)
        stopService(Intent(this, WifiServerService::class.java))
        if (connectionInfoAvailable){
            removeGroup()
        }
    }

    private fun initView(){
        setTitle("Receive file")
        ivImage = findViewById(R.id.iv_image)
        tvLog = findViewById(R.id.tv_log)
        findViewById<View>(R.id.tv_createGroup).setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
            ){
                return@setOnClickListener
            }
            wifiP2pManager.createGroup(channel,object :WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    LogUtil.d(TAG,"onSuccess")
                    dismissLoadingDialog()
                    showToast("onSuccess")
                }

                override fun onFailure(reason: Int) {
                    LogUtil.e(TAG,"onFailure $reason")
                    dismissLoadingDialog()
                    showToast("onFailure $reason")
                }
            })
        }
        findViewById<View>(R.id.btn_removeGroup).setOnClickListener {
            removeGroup()
        }
        // 配置dialog
        processDialog = ProgressDialog(this)
        processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        processDialog.setCancelable(false)
        processDialog.setCanceledOnTouchOutside(false)
        processDialog.setTitle("正在接受文件")
        processDialog.max = 100
    }

    private val serviceConnection:ServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            val binder = service as WifiServerBinder
            wifiServerService = binder.service
            wifiServerService.setProgressChangListener(progressChangListener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            if (wifiServerService != null) {
                wifiServerService.setProgressChangListener(null)
                //wifiServerService = null
            }
            bindService()
        }
    }

    private val progressChangListener:OnProgressChangeListener = object :OnProgressChangeListener{
        override fun onProgressChanged(fileTransfer: FileTransfer, progress: Int) {
            // 主线程刷新
            runOnUiThread {
                processDialog.setMessage("FileName: " + fileTransfer.getFileName())
                processDialog.progress = progress
                processDialog.show()
            }
        }

        override fun onTransferFinished(file: File) {
            runOnUiThread {
                processDialog.cancel()
                if (null != file && file.exists()) {
                    Glide.with(this@ReceiveFileActivity).load(file.path).into(ivImage)
                }
            }
        }
    }

    private val actionListener: WifiActionListener = object : WifiActionListener {
        override fun wifiP2pEnabled(enabled: Boolean) {
            LogUtil.e(TAG,"wifiP2pEnabled $enabled")
        }

        override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {
            LogUtil.d(TAG,"onConnectionInfoAvailable,isGroupOwner=${wifiP2pInfo.isGroupOwner},groupFormed=${wifiP2pInfo.groupFormed}")
            if (wifiP2pInfo.isGroupOwner && wifiP2pInfo.groupFormed){
                connectionInfoAvailable = true
                if (wifiServerService != null){
                    startService(WifiServerService::class.java)
                }
            }
        }

        override fun onDisconnection() {
            connectionInfoAvailable = false
            LogUtil.d(TAG,"onDisconnection")
        }

        override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {
            LogUtil.d(TAG,"onSelfDeviceAvailable,devices is $wifiP2pDevice")
        }

        override fun onPeersAvailable(wifiP2pDeviceList: List<WifiP2pDevice>) {
            LogUtil.d(TAG,"onPeersAvailable,size:" + wifiP2pDeviceList.size)
            for (wifiP2pDevice in wifiP2pDeviceList) {
                LogUtil.d(TAG, wifiP2pDevice.toString())
            }
        }

        override fun onChannelDisconnected() {
            LogUtil.d(TAG,"onChannelDisconnected")
        }
    }

    private fun removeGroup(){
        wifiP2pManager.removeGroup(channel,object :WifiP2pManager.ActionListener{
            override fun onSuccess() {
                LogUtil.d(TAG,"onSuccess")
                showToast("onSuccess")
            }

            override fun onFailure(p0: Int) {
                LogUtil.e(TAG,"onFailure")
                showToast("onFailure")
            }
        })
    }

    private fun bindService(){
        bindService(Intent(this, WifiServerService::class.java),
            serviceConnection, BIND_AUTO_CREATE)
    }
}