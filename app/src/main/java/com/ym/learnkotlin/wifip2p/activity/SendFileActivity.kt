package com.ym.learnkotlin.wifip2p.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore

import android.content.Intent

import android.net.wifi.p2p.WifiP2pManager

import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat


import android.net.wifi.WpsInfo

import android.net.wifi.p2p.WifiP2pConfig

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

import android.net.wifi.p2p.WifiP2pDevice

import android.net.wifi.p2p.WifiP2pInfo

import android.content.BroadcastReceiver
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button

import android.widget.TextView
import com.ym.learnkotlin.R
import com.ym.learnkotlin.util.LogUtil
import com.ym.learnkotlin.wifip2p.*
import com.ym.learnkotlin.wifip2p.api.WifiActionListener
import com.ym.learnkotlin.wifip2p.service.WifiP2pBroadcastReceiver
import com.ym.learnkotlin.wifip2p.util.WifiP2pUtil
import java.lang.StringBuilder


class SendFileActivity : BaseActivity() {
    companion object {
        private const val TAG = "SendFileActivity"
        private const val CODE_CHOOSE_FILE = 100
    }

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var wifiP2pInfo: WifiP2pInfo
    private var wifiP2pEnabled = false
    private lateinit var wifiP2pDeviceList: MutableList<WifiP2pDevice>
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var tv_myDeviceName: TextView
    private lateinit var tv_myDeviceAddress: TextView
    private lateinit var tv_myDeviceStatus: TextView
    private lateinit var tv_status: TextView
    private lateinit var btn_disconnect: Button
    private lateinit var btn_chooseFile: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var mWifiP2pDevice: WifiP2pDevice



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_file)
        initView()
        initEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                val imageUri: Uri? = data!!.data
                Log.e(TAG, "文件路径：$imageUri")
                WifiClientTask(this).execute(
                    wifiP2pInfo.groupOwnerAddress.hostAddress,
                    imageUri
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.menuDirectEnable) {
            if (wifiP2pManager != null && channel != null) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } else {
                showToast("当前设备不支持Wifi Direct")
            }
            return true
        } else if (id == R.id.menuDirectDiscover) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showToast("请先授予位置权限")
                return true
            }
            if (!wifiP2pEnabled) {
                showToast("需要先打开Wifi")
                return true
            }
            loadingDialog.show("正在搜索附近设备", true, true)
            wifiP2pDeviceList.clear()
            deviceAdapter.notifyDataSetChanged()
            // 搜寻附近带有 WiFi P2P 的设备
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    showToast("Success")
                    LogUtil.d(TAG," WifiDeviceList $wifiP2pDeviceList")
                }

                override fun onFailure(reasonCode: Int) {
                    showToast("Failure")
                    loadingDialog.cancel()
                }
            })
            return true
        }
        return true
    }

    private fun initEvent() {
        wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        if (wifiP2pManager == null) {
            finish()
            return
        }
        channel = wifiP2pManager.initialize(this, mainLooper, directActionListener)
        broadcastReceiver = WifiP2pBroadcastReceiver(wifiP2pManager, channel, directActionListener)
        registerReceiver(broadcastReceiver, WifiP2pBroadcastReceiver.getIntentFilter())
        LogUtil.d(TAG,"Init channel=$channel and registerReceiver")
    }

    private fun initView() {
        setTitle("发送文件")
        tv_myDeviceName = findViewById(R.id.tv_myDeviceName)
        tv_myDeviceAddress = findViewById(R.id.tv_myDeviceAddress)
        tv_myDeviceStatus = findViewById(R.id.tv_myDeviceStatus)
        tv_status = findViewById(R.id.tv_status)
        btn_disconnect = findViewById(R.id.btn_disconnect)
        btn_chooseFile = findViewById(R.id.btn_chooseFile)

        loadingDialog = LoadingDialog(this)
        wifiP2pDeviceList = ArrayList()
        deviceAdapter = DeviceAdapter(wifiP2pDeviceList)
        deviceAdapter.setClickListener(object :DeviceAdapter.OnClickListener{
            override fun onItemClick(position: Int) {
                mWifiP2pDevice = wifiP2pDeviceList[position]
                showToast(mWifiP2pDevice.deviceName)
                connect()
            }

        })

        val rvDeviceList = findViewById<RecyclerView>(R.id.rv_deviceList)
        rvDeviceList.adapter = deviceAdapter
        rvDeviceList.layoutManager = LinearLayoutManager(this)

        val clickListener: View.OnClickListener = View.OnClickListener { v ->
            val id: Int = v.id
            if (id == R.id.btn_disconnect) {
                disconnect()
            } else if (id == R.id.btn_chooseFile) {
                navToChosePicture()
            }
        }
        btn_disconnect.setOnClickListener(clickListener)
        btn_chooseFile.setOnClickListener(clickListener)
    }

    private val directActionListener: WifiActionListener = object : WifiActionListener {
        override fun wifiP2pEnabled(enabled: Boolean) {
            wifiP2pEnabled = enabled
        }

        override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {
            dismissLoadingDialog()
            wifiP2pDeviceList.clear()
            deviceAdapter.notifyDataSetChanged()
            btn_disconnect.isEnabled = true
            btn_chooseFile.isEnabled = true

            Log.e(TAG, "onConnectionInfoAvailable")
            Log.e(TAG, "onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed)
            Log.e(TAG, "onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner)
            Log.e(TAG, "onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.hostAddress)

            val stringBuilder = StringBuilder()
            if (mWifiP2pDevice != null) {
                stringBuilder.append("连接的设备名：")
                stringBuilder.append(mWifiP2pDevice.deviceName)
                stringBuilder.append("\n")
                stringBuilder.append("连接的设备的地址：")
                stringBuilder.append(mWifiP2pDevice.deviceAddress)
            }
            stringBuilder.append("\n")
            stringBuilder.append("是否群主：")
            stringBuilder.append(if (wifiP2pInfo.isGroupOwner) "是群主" else "非群主")
            stringBuilder.append("\n")
            stringBuilder.append("群主IP地址：")
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.hostAddress)
            tv_status.text = stringBuilder
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                this@SendFileActivity.wifiP2pInfo = wifiP2pInfo
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onDisconnection() {
            Log.e(TAG, "onDisconnection")
            btn_disconnect.isEnabled = false
            btn_chooseFile.isEnabled = false
            showToast("处于非连接状态")
            wifiP2pDeviceList.clear()
            deviceAdapter.notifyDataSetChanged()
            tv_status.setText(null)
            //wifiP2pInfo = null
        }

        override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable")
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName)
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress)
            Log.e(TAG, "Status: " + wifiP2pDevice.status)
            tv_myDeviceName.text = wifiP2pDevice.deviceName
            tv_myDeviceAddress.text = wifiP2pDevice.deviceAddress
            tv_myDeviceStatus.text = WifiP2pUtil.getDeviceStatus(wifiP2pDevice.status)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onPeersAvailable(wifiP2pDeviceList: List<WifiP2pDevice>) {
            Log.e(TAG, "onPeersAvailable :" + wifiP2pDeviceList.size)
            this@SendFileActivity.wifiP2pDeviceList.clear()
            this@SendFileActivity.wifiP2pDeviceList.addAll(wifiP2pDeviceList)
            deviceAdapter.notifyDataSetChanged()
            loadingDialog.cancel()
        }

        override fun onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected")
        }
    }

    private fun connect() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showToast("请先授予位置权限")
            return
        }
        val config = WifiP2pConfig()
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress
            config.wps.setup = WpsInfo.PBC
            showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName)
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.e(TAG, "connect onSuccess")
                }

                override fun onFailure(reason: Int) {
                    showToast("连接失败 $reason")
                    dismissLoadingDialog()
                }
            })
        }
    }

    private fun disconnect() {
        wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onFailure(reasonCode: Int) {
                Log.e(TAG, "disconnect onFailure:$reasonCode")
            }

            override fun onSuccess() {
                Log.e(TAG, "disconnect onSuccess")
                tv_status.text = null
                btn_disconnect.isEnabled = false
                btn_chooseFile.isEnabled = false
            }
        })
    }

    private fun navToChosePicture() {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, CODE_CHOOSE_FILE)
    }

}