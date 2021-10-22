package com.ym.learnkotlin.design.simplefactory

import java.lang.IllegalArgumentException

class ComputerFactory {
    companion object {
        fun crateComputer(type: String): Computer? {

        var mComputer: Computer? = null
        when(type)
        {
            "lenovo"-> mComputer = LenovoComputer()
            "hp"-> mComputer = HPComputer()
            else -> throw IllegalArgumentException("ComputerFactory illegal type,type is $type")
        }
        return mComputer
    }
    }
}