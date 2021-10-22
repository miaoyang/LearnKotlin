package com.ym.learnkotlin.design.buildpattern

abstract class Builder {
    abstract fun buildCpu(cpu:String?)
    abstract fun buildMainboard(mainboard:String?)
    abstract fun buildRam(ram:String?)
    abstract fun create():Computer
}