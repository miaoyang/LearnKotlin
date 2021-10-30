package com.ym.learnkotlin.annotation

import android.app.Activity
import com.ym.learnkotlin.util.LogUtil
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

class ViewInjectUtil {
    companion object{
        private const val TAG:String = "ViewInjectUtil"
        private const val METHOD_SET_CONTENTVIEW = "setContentView"
        private const val METHOD_SET_FIND_VIEW_BY_ID = "findViewById"
    }


    private fun injectContentView(activity:Activity){
        var clazz  = activity.javaClass
        var contentView:ContentView = clazz.getAnnotation(ContentView::class.java)
        if (null != contentView){
            var contentViewLayoutId = contentView.value
            try {
                var method:Method = clazz.getMethod(METHOD_SET_CONTENTVIEW,Int.javaClass)
                method.invoke(activity,contentViewLayoutId)
            }catch (e:Exception){
                LogUtil.e(TAG,"injectContentView error $e")
            }
        }
    }

    private fun injectView(activity: Activity){
        var clazz = activity.javaClass
        var fields = clazz.declaredFields
        for (field in fields){
            var viewInject:ViewInject = field.getAnnotation(ViewInject::class.java)
            if (null != viewInject){
                var viewId:Int = viewInject.value
                if (viewId != -1){
                    try {
                        var method:Method = clazz.getMethod(METHOD_SET_FIND_VIEW_BY_ID,Int.javaClass)
                        var any:Any = method.invoke(activity,viewId)
                        field.set(activity,any)
                    }catch (e:Exception){
                        LogUtil.e(TAG,"injectView error $e")
                    }
                }
            }
        }

    }

    fun inject(activity: Activity){
        injectContentView(activity)
        injectView(activity)
    }
}