package com.ym.learnkotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.ym.learnkotlin.annotation.ContentView
import com.ym.learnkotlin.annotation.ViewInject
import com.ym.learnkotlin.databinding.ActivityMainBinding
import com.ym.learnkotlin.wifip2p.activity.MainActivityWifiP2p


class MainActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var mWifiP2p:Button

    private lateinit var mBtnGo:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWifiP2p = findViewById(R.id.btn_wifip2p)
        mWifiP2p.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_wifip2p ->{
                startActivity(Intent(this,MainActivityWifiP2p::class.java))
                Toast.makeText(this,"Button WifiP2p",Toast.LENGTH_SHORT)
            }
            R.id.btn_go ->{
                Toast.makeText(this,"Button Go",Toast.LENGTH_SHORT)
            }
        }
    }

}