package com.ym.learnkotlin.design.simplefactory

import androidx.navigation.NavArgs

class CreateComputer {
    fun main(args: Array<String>){
        ComputerFactory.crateComputer("lenovo")?.start()
    }
}