package com.ym.learnkotlin.design.simplefactory

class LenovoComputer:Computer() {
    override fun start() {
        println("Lenovo computer")
    }
}