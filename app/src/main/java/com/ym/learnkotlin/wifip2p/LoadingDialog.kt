package com.ym.learnkotlin.wifip2p

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.view.animation.Animation

import androidx.annotation.StringRes

import android.view.animation.AnimationUtils
import com.ym.learnkotlin.R

class LoadingDialog(context: Context?) :
    Dialog(context!!, R.style.LoadingDialogTheme) {

    private val ivLoading: ImageView
    private val tvHint: TextView
    private val animation: Animation

    init {
        setContentView(R.layout.dialog_loading)
        ivLoading = findViewById(R.id.iv_loading)
        tvHint = findViewById(R.id.tv_hint)
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_dialog)
    }

    fun show(hintText: String?, cancelable: Boolean, canceledOnTouchOutside: Boolean) {
        setCancelable(cancelable)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        tvHint.text = hintText
        ivLoading.startAnimation(animation)
        show()
    }

    override fun cancel() {
        super.cancel()
        animation.cancel()
        ivLoading.clearAnimation()
    }

    override fun dismiss() {
        super.dismiss()
        animation.cancel()
        ivLoading.clearAnimation()
    }

}