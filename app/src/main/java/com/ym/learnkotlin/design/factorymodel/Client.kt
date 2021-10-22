package com.ym.learnkotlin.design.factorymodel

import com.ym.learnkotlin.design.simplefactory.LenovoComputer

class Client {
    fun main(args:Array<String>){
        var computerFactory:ComputerFactory = GDComputerFactory()
        var lenovoComputer:LenovoComputer = computerFactory.createComputer(LenovoComputer::class.java)
        lenovoComputer.start()

    }
}