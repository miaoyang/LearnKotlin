package com.ym.learnkotlin.wifip2p.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

import android.content.Intent

import android.app.Activity
import android.app.Service
import com.ym.learnkotlin.wifip2p.LoadingDialog


open class BaseActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog

    protected fun setTitle(title:String){
        var actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = title
        }
    }

    protected fun showLoadingDialog(msg:String){
        if (msg==null){
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog.show(msg,true,false)
    }
    protected fun dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss()
        }
    }

    protected fun <T : Activity?> startActivity(tClass: Class<T>?) {
        startActivity(Intent(this, tClass))
    }

    protected fun <T : Service?> startService(tClass: Class<T>?) {
        startService(Intent(this, tClass))
    }

    protected fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}