package com.ym.learnkotlin.annotation

import android.app.Activity
import java.lang.Exception
import java.lang.reflect.Method

class ViewInjectUtil {
    private val METHOD_SET_CONTENTVIEW = "setContentView"
    private fun injectContentView(activity:Activity){
        var clazz  = activity.javaClass
        var contentView:ContentView = clazz.getAnnotation(ContentView::class.java)
        if (null != contentView){
            var contentViewLayoutId = contentView.value
            try {
                var method:Method = clazz.getMethod(METHOD_SET_CONTENTVIEW,)
            }catch (ignore:Exception){}
        }
    }
}