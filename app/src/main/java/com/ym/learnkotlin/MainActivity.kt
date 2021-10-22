package com.ym.learnkotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import com.ym.learnkotlin.annotation.ContentView
import com.ym.learnkotlin.annotation.ViewInject
import com.ym.learnkotlin.databinding.ActivityMainBinding

@ContentView(R.layout.activity_main)
class MainActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    @ViewInject(R.id.btn_stop)
    private lateinit var mBtnStop:Button

    @ViewInject(R.id.btn_go)
    private lateinit var mBtnGo:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_stop ->{
                Toast.makeText(this,"Button Stop",Toast.LENGTH_SHORT)
            }
            R.id.btn_go ->{
                Toast.makeText(this,"Button Go",Toast.LENGTH_SHORT)
            }
        }
    }

}