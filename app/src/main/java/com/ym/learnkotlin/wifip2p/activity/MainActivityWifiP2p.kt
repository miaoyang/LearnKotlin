package com.ym.learnkotlin.wifip2p.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.ym.learnkotlin.R


import android.content.Intent
import android.content.pm.PackageManager


@SuppressLint("WrongViewCast")
class MainActivityWifiP2p: BaseActivity() {
    companion object{
        private const val CODE_REQ_PERMISSIONS = 665
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wifip2p)
        setTitle("Wifi P2P")
        findViewById<View>(R.id.btnCheckPermission).setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), CONTEXT_INCLUDE_CODE)
        }

        findViewById<View>(R.id.btnSender).setOnClickListener {
            startActivity(Intent(this, SendFileActivity::class.java))
        }

        findViewById<View>(R.id.btnReceiver).setOnClickListener {
            startActivity(Intent(this,ReceiveFileActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== CODE_REQ_PERMISSIONS){
            for (grantResult in grantResults){
                if (grantResult!=PackageManager.PERMISSION_GRANTED){
                    showToast("Lack of permission,please grant permission $grantResult")
                    return
                }
            }
            showToast("Permission granted")
        }
    }
}

