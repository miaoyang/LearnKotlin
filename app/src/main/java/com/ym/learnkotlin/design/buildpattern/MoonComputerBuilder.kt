package com.ym.learnkotlin.design.buildpattern

class MoonComputerBuilder: Builder() {
    private val mComputer:Computer = Computer()
    override fun buildCpu(cpu: String?) {

    }

    override fun buildMainboard(mainboard: String?) {

    }

    override fun buildRam(ram: String?) {

    }

    override fun create(): Computer {
        return mComputer
    }
}