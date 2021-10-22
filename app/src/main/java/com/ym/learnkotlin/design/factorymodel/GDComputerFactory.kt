package com.ym.learnkotlin.design.factorymodel

import com.ym.learnkotlin.util.LogUtil
import java.lang.Exception

class GDComputerFactory: ComputerFactory() {
    companion object{
        private val TAG:String = GDComputerFactory.javaClass.simpleName
    }
    override fun <T> createComputer(clz: Class<T>): T {
        var computer:Computer?=null
        val className:String = clz.name
        try {
            computer = Class.forName(className).newInstance() as Computer?
        }catch (e:Exception){
            LogUtil.d(TAG,"GDComputerFactory e $e")
        }
        return computer as T
    }
}